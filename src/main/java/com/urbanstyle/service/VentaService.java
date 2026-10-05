package com.urbanstyle.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.urbanstyle.config.AppConfig;
import com.urbanstyle.config.JPAUtil;
import com.urbanstyle.dto.LineaVenta;
import com.urbanstyle.dto.ResultadoVenta;
import com.urbanstyle.dto.VentaSolicitud;
import com.urbanstyle.entity.Cliente;
import com.urbanstyle.entity.ComprobantePago;
import com.urbanstyle.entity.DetalleVenta;
import com.urbanstyle.entity.EstadoVenta;
import com.urbanstyle.entity.ProductoVariante;
import com.urbanstyle.entity.TipoComprobante;
import com.urbanstyle.entity.Usuario;
import com.urbanstyle.entity.Venta;
import com.urbanstyle.util.Texto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

/**
 * Reglas de negocio de las ventas.
 *
 * registrar() hace TODO dentro de una sola transacción JPA:
 * bloquea las variantes (SELECT ... FOR UPDATE), valida stock, guarda
 * la venta con sus detalles y su comprobante (cascade) y descuenta stock.
 * Si algo falla, JPAUtil hace rollback y no queda nada a medias.
 *
 * Las consultas y la anulación de ventas están en HistorialService.
 */
public class VentaService {

    /** SUNAT: una boleta mayor a S/ 700 debe identificar al cliente con DNI. */
    public static final BigDecimal MONTO_BOLETA_CON_DNI = new BigDecimal("700.00");
    private static final int MAX_CANTIDAD_POR_LINEA = 999;

    // ------------------------------------------------------------------
    // Cálculo previo (sin guardar)
    // ------------------------------------------------------------------

    public ResultadoVenta calcular(VentaSolicitud solicitud) {
        Map<Integer, Integer> items = consolidarItems(solicitud);
        TipoComprobante tipo = leerTipo(solicitud.getTipoComprobante());
        return JPAUtil.consultar(em -> {
            List<ProductoVariante> variantes = new ArrayList<>();
            List<Integer> cantidades = new ArrayList<>();
            for (Map.Entry<Integer, Integer> item : items.entrySet()) {
                variantes.add(cargarVariante(em, item.getKey(), false));
                cantidades.add(item.getValue());
            }
            ResultadoVenta resultado = armarResultado(variantes, cantidades, false);
            if (resultado.isOk()) {
                Cliente cliente = solicitud.getClienteId() == null ? null
                        : em.find(Cliente.class, solicitud.getClienteId());
                // Valida el DNI/RUC antes de pedir confirmación (no asigna número).
                documentoValidado(tipo, solicitud.getDocumento(), cliente, resultado.getTotal());
                resultado.setMensaje("Cálculo realizado.");
            }
            return resultado;
        });
    }

    // ------------------------------------------------------------------
    // Registro de la venta
    // ------------------------------------------------------------------

    public ResultadoVenta registrar(VentaSolicitud solicitud, int usuarioId) {
        Map<Integer, Integer> items = consolidarItems(solicitud);
        TipoComprobante tipo = leerTipo(solicitud.getTipoComprobante());

        return JPAUtil.enTransaccion(em -> {
            Usuario vendedor = em.find(Usuario.class, usuarioId);
            if (vendedor == null || !vendedor.isActivo()) {
                throw new ServiceException("El usuario de la sesión no está activo.");
            }

            Cliente cliente = null;
            if (solicitud.getClienteId() != null) {
                cliente = em.find(Cliente.class, solicitud.getClienteId());
                if (cliente == null || !cliente.isActivo()) {
                    throw new ServiceException("El cliente seleccionado no existe o está inactivo.");
                }
            }

            // 1) Bloquear variantes en orden de id (evita interbloqueos) y validar stock.
            List<ProductoVariante> variantes = new ArrayList<>();
            List<Integer> cantidades = new ArrayList<>();
            for (Map.Entry<Integer, Integer> item : items.entrySet()) {
                variantes.add(cargarVariante(em, item.getKey(), true));
                cantidades.add(item.getValue());
            }
            ResultadoVenta resultado = armarResultado(variantes, cantidades, true);

            // 2) Comprobante: validar documento del cliente.
            ComprobantePago comprobante = crearComprobante(em, tipo, solicitud.getDocumento(),
                    cliente, resultado.getTotal());

            // 3) Armar la venta con sus detalles (relación bidireccional).
            Venta venta = new Venta();
            venta.setUsuario(vendedor);
            venta.setCliente(cliente);
            venta.setSubtotal(resultado.getSubtotal());
            venta.setIgv(resultado.getIgv());
            venta.setTotal(resultado.getTotal());
            venta.setEstado(EstadoVenta.COMPLETADA);

            for (int i = 0; i < variantes.size(); i++) {
                ProductoVariante variante = variantes.get(i);
                LineaVenta linea = resultado.getLineas().get(i);

                DetalleVenta detalle = new DetalleVenta();
                detalle.setVariante(variante);
                detalle.setCantidad(linea.getCantidad());
                detalle.setPrecioUnitario(linea.getPrecioUnitario());
                detalle.setTotalLinea(linea.getTotalLinea());
                venta.agregarDetalle(detalle);

                // 4) Descontar stock: la variante está administrada, el UPDATE sale en el commit.
                variante.setStock(variante.getStock() - linea.getCantidad());
            }
            venta.asignarComprobante(comprobante);

            // 5) Un solo persist: cascade guarda sale_details y payment_documents.
            em.persist(venta);
            em.flush();

            resultado.setVentaId(venta.getId());
            resultado.setComprobante(comprobante.getNumeroCompleto());
            resultado.setTipoComprobante(tipo.name());
            resultado.setMensaje("Venta registrada: " + tipo.getEtiqueta() + " " + comprobante.getNumeroCompleto());
            return resultado;
        });
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    /** Valida los ítems y suma cantidades si una variante viene repetida. */
    private Map<Integer, Integer> consolidarItems(VentaSolicitud solicitud) {
        if (solicitud == null || solicitud.getItems() == null || solicitud.getItems().isEmpty()) {
            throw new ServiceException("Agrega al menos un producto a la venta.");
        }
        Map<Integer, Integer> items = new TreeMap<>(); // ordenado por id
        for (VentaSolicitud.Item item : solicitud.getItems()) {
            if (item == null || item.getVarianteId() == null || item.getVarianteId() <= 0) {
                throw new ServiceException("Hay un producto no válido en la venta.");
            }
            int cantidad = item.getCantidad() == null ? 0 : item.getCantidad();
            if (cantidad <= 0) {
                throw new ServiceException("Las cantidades deben ser mayores que cero.");
            }
            int acumulada = items.getOrDefault(item.getVarianteId(), 0) + cantidad;
            if (acumulada > MAX_CANTIDAD_POR_LINEA) {
                throw new ServiceException("La cantidad máxima por producto es " + MAX_CANTIDAD_POR_LINEA + ".");
            }
            items.put(item.getVarianteId(), acumulada);
        }
        return items;
    }

    private ProductoVariante cargarVariante(EntityManager em, int varianteId, boolean bloquear) {
        ProductoVariante variante = bloquear
                ? em.find(ProductoVariante.class, varianteId, LockModeType.PESSIMISTIC_WRITE)
                : em.find(ProductoVariante.class, varianteId);
        if (variante == null) {
            throw new ServiceException("El producto seleccionado (variante " + varianteId + ") ya no existe.");
        }
        if (!variante.isActivo() || !variante.getProducto().isActivo()) {
            throw new ServiceException(nombre(variante) + " no está disponible para la venta.");
        }
        return variante;
    }

    private ResultadoVenta armarResultado(List<ProductoVariante> variantes, List<Integer> cantidades,
                                          boolean exigirStock) {
        ResultadoVenta resultado = new ResultadoVenta();
        List<BigDecimal> totalesLinea = new ArrayList<>();
        List<String> sinStock = new ArrayList<>();

        for (int i = 0; i < variantes.size(); i++) {
            ProductoVariante v = variantes.get(i);
            int cantidad = cantidades.get(i);
            if (cantidad > v.getStock()) {
                sinStock.add(nombre(v) + " (disponible: " + v.getStock() + ")");
            }
            BigDecimal precio = CalculadoraVenta.dinero(v.getProducto().getPrecio());
            BigDecimal totalLinea = CalculadoraVenta.totalLinea(precio, cantidad);
            totalesLinea.add(totalLinea);

            LineaVenta linea = new LineaVenta();
            linea.setVarianteId(v.getId());
            linea.setProducto(v.getProducto().getNombre());
            linea.setColor(v.getColor().getNombre());
            linea.setTalla(v.getTalla().getNombre());
            linea.setImagenUrl(v.getImagenUrl());
            linea.setCantidad(cantidad);
            linea.setStockDisponible(v.getStock());
            linea.setPrecioUnitario(precio);
            linea.setTotalLinea(totalLinea);
            resultado.getLineas().add(linea);
        }

        if (!sinStock.isEmpty()) {
            String mensaje = "Stock insuficiente: " + String.join(", ", sinStock) + ".";
            if (exigirStock) {
                throw new ServiceException(mensaje);
            }
            resultado.setOk(false);
            resultado.setMensaje(mensaje);
        }

        BigDecimal porcentaje = AppConfig.igvPorcentaje();
        CalculadoraVenta.Totales totales = CalculadoraVenta.calcular(totalesLinea, porcentaje);
        resultado.setIgvPorcentaje(porcentaje);
        resultado.setSubtotal(totales.subtotal());
        resultado.setIgv(totales.igv());
        resultado.setTotal(totales.total());
        return resultado;
    }

    private TipoComprobante leerTipo(String valor) {
        if (Texto.vacio(valor)) {
            return TipoComprobante.BOLETA;
        }
        try {
            return TipoComprobante.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Tipo de comprobante no válido: " + valor);
        }
    }

    private ComprobantePago crearComprobante(EntityManager em, TipoComprobante tipo, String documento,
                                             Cliente cliente, BigDecimal total) {
        String doc = documentoValidado(tipo, documento, cliente, total);
        ComprobantePago comprobante = new ComprobantePago();
        comprobante.setTipo(tipo);
        if (tipo == TipoComprobante.FACTURA) {
            comprobante.setRucCliente(doc);
            comprobante.setSerie(AppConfig.serieFactura());
        } else {
            comprobante.setDniCliente(doc);
            comprobante.setSerie(AppConfig.serieBoleta());
        }
        comprobante.setNumero(siguienteNumero(em, tipo, comprobante.getSerie()));
        return comprobante;
    }

    /**
     * Factura: RUC obligatorio. Boleta: DNI opcional (si no se indica se usa
     * el del cliente), pero obligatorio cuando el total supera S/ 700.
     * Devuelve el documento que se imprimirá en el comprobante.
     */
    private String documentoValidado(TipoComprobante tipo, String documento, Cliente cliente, BigDecimal total) {
        String doc = Texto.limpiar(documento);
        if (tipo == TipoComprobante.FACTURA) {
            if (!Texto.esRuc(doc)) {
                throw new ServiceException("Para emitir factura ingresa un RUC válido de 11 dígitos.");
            }
            return doc;
        }
        if (doc == null && cliente != null) {
            doc = cliente.getDni();
        }
        if (doc != null && !Texto.esDni(doc)) {
            throw new ServiceException("El DNI de la boleta debe tener 8 dígitos.");
        }
        if (doc == null && total.compareTo(MONTO_BOLETA_CON_DNI) > 0) {
            throw new ServiceException("Las boletas mayores a S/ 700.00 requieren el DNI del cliente.");
        }
        return doc;
    }

    /**
     * Correlativo por (tipo, serie). Se bloquea el último comprobante de la
     * serie para que dos cajas no obtengan el mismo número.
     */
    private int siguienteNumero(EntityManager em, TipoComprobante tipo, String serie) {
        List<ComprobantePago> ultimos = em.createQuery(
                        "SELECT c FROM ComprobantePago c WHERE c.tipo = :tipo AND c.serie = :serie"
                        + " ORDER BY c.numero DESC", ComprobantePago.class)
                .setParameter("tipo", tipo)
                .setParameter("serie", serie)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(1)
                .getResultList();
        return ultimos.isEmpty() ? 1 : ultimos.get(0).getNumero() + 1;
    }

    private static String nombre(ProductoVariante v) {
        return v.getProducto().getNombre() + " " + v.getDescripcionCorta();
    }
}
