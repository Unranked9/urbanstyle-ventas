package com.urbanstyle.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.urbanstyle.config.JPAUtil;
import com.urbanstyle.dto.FilaReporteCliente;
import com.urbanstyle.dto.FiltroVentas;
import com.urbanstyle.dto.ResumenVentas;
import com.urbanstyle.entity.Cliente;
import com.urbanstyle.entity.EstadoVenta;
import com.urbanstyle.entity.Venta;
import com.urbanstyle.util.Texto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

public class ReporteService {

    public static final int MAXIMO_FILAS_VENTAS = 5000;

    private static final long MAXIMO_DIAS = 366;

    public static class ReporteClientes {
        private final List<FilaReporteCliente> filas;
        private long mayoristas;
        private long minoristas;
        private long compras;
        private BigDecimal totalComprado = BigDecimal.ZERO;

        public ReporteClientes(List<FilaReporteCliente> filas) {
            this.filas = filas;
            for (FilaReporteCliente f : filas) {
                if (f.getCliente().isMayorista()) {
                    mayoristas++;
                } else {
                    minoristas++;
                }
                compras += f.getCompras();
                totalComprado = totalComprado.add(f.getTotalComprado());
            }
        }

        public List<FilaReporteCliente> getFilas() {
            return filas;
        }

        public int getTotalClientes() {
            return filas.size();
        }

        public long getMayoristas() {
            return mayoristas;
        }

        public long getMinoristas() {
            return minoristas;
        }

        public long getCompras() {
            return compras;
        }

        public BigDecimal getTotalComprado() {
            return totalComprado;
        }
    }

    public ReporteClientes reporteClientes(String texto, Boolean activo, Boolean mayorista) {
        String filtroTexto = Texto.limpiar(texto);
        return JPAUtil.consultar(em -> {
            StringBuilder jpql = new StringBuilder("SELECT c FROM Cliente c WHERE 1 = 1");
            if (filtroTexto != null) {
                jpql.append(" AND (LOWER(c.nombres) LIKE :texto OR LOWER(c.apellidos) LIKE :texto")
                    .append(" OR LOWER(CONCAT(c.nombres, ' ', c.apellidos)) LIKE :texto")
                    .append(" OR c.dni LIKE :texto OR LOWER(c.email) LIKE :texto)");
            }
            if (activo != null) {
                jpql.append(" AND c.activo = :activo");
            }
            if (mayorista != null) {
                jpql.append(" AND c.mayorista = :mayorista");
            }
            jpql.append(" ORDER BY c.nombres, c.apellidos");

            TypedQuery<Cliente> q = em.createQuery(jpql.toString(), Cliente.class);
            if (filtroTexto != null) {
                q.setParameter("texto", "%" + filtroTexto.toLowerCase() + "%");
            }
            if (activo != null) {
                q.setParameter("activo", activo);
            }
            if (mayorista != null) {
                q.setParameter("mayorista", mayorista);
            }
            List<Cliente> clientes = q.getResultList();
            Map<Integer, Object[]> compras = new HashMap<>();
            List<Object[]> filas = em.createQuery(
                    "SELECT v.cliente.id, COUNT(v), SUM(v.total) FROM Venta v"
                    + " WHERE v.cliente IS NOT NULL AND v.estado = :completada"
                    + " GROUP BY v.cliente.id", Object[].class)
                    .setParameter("completada", EstadoVenta.COMPLETADA)
                    .getResultList();
            for (Object[] fila : filas) {
                compras.put(((Number) fila[0]).intValue(), fila);
            }
            List<FilaReporteCliente> resultado = new ArrayList<>();
            for (Cliente c : clientes) {
                Object[] fila = compras.get(c.getId());
                long cantidad = fila == null ? 0 : ((Number) fila[1]).longValue();
                BigDecimal total = fila == null ? BigDecimal.ZERO : (BigDecimal) fila[2];
                resultado.add(new FilaReporteCliente(c, cantidad, total));
            }
            return new ReporteClientes(resultado);
        });
    }

    public static class ReporteVentas {
        private final List<Venta> ventas;
        private final Map<Integer, Long> prendas;
        private final ResumenVentas resumen;

        public ReporteVentas(List<Venta> ventas, Map<Integer, Long> prendas, ResumenVentas resumen) {
            this.ventas = ventas;
            this.prendas = prendas;
            this.resumen = resumen;
        }

        public List<Venta> getVentas() {
            return ventas;
        }

        public Map<Integer, Long> getPrendas() {
            return prendas;
        }

        public ResumenVentas getResumen() {
            return resumen;
        }

        public boolean isTruncado() {
            return resumen.getRegistros() > ventas.size();
        }
    }

    public static ReporteVentas reporteVentasVacio() {
        return new ReporteVentas(new ArrayList<>(), new HashMap<>(), new ResumenVentas());
    }

    public ReporteVentas reporteVentas(FiltroVentas f) {
        validarFechas(f);
        return JPAUtil.consultar(em -> {
            String where = condiciones(f, "");
            TypedQuery<Venta> q = em.createQuery(
                    "SELECT v FROM Venta v JOIN FETCH v.usuario u"
                    + " LEFT JOIN FETCH v.cliente c LEFT JOIN FETCH v.comprobante cp"
                    + where + " ORDER BY v.fecha, v.id", Venta.class);
            parametros(q, f);
            List<Venta> ventas = q.setMaxResults(MAXIMO_FILAS_VENTAS).getResultList();
            Map<Integer, Long> prendas = prendasPorVenta(em, ventas);
            ResumenVentas resumen = resumen(em, f);
            return new ReporteVentas(ventas, prendas, resumen);
        });
    }

    private void validarFechas(FiltroVentas f) {
        if (f.getDesde() == null || f.getHasta() == null) {
            throw new ServiceException("Selecciona la fecha inicial y la fecha final.");
        }
        if (f.getDesde().isAfter(f.getHasta())) {
            throw new ServiceException("La fecha inicial no puede ser mayor que la fecha final.");
        }
        if (ChronoUnit.DAYS.between(f.getDesde(), f.getHasta()) > MAXIMO_DIAS) {
            throw new ServiceException("El rango del reporte no puede ser mayor a un año.");
        }
    }
    private static String condiciones(FiltroVentas f, String extra) {
        StringBuilder w = new StringBuilder(" WHERE v.fecha >= :desde AND v.fecha < :hasta");
        if (f.getEstado() != null) {
            w.append(" AND v.estado = :estado");
        }
        if (f.getTipoComprobante() != null) {
            w.append(" AND cp.tipo = :tipo");
        }
        if (f.getUsuarioId() != null) {
            w.append(" AND u.id = :usuarioId");
        }
        return w.append(extra).toString();
    }

    private static void parametros(Query q, FiltroVentas f) {
        q.setParameter("desde", f.getDesde().atStartOfDay());
        q.setParameter("hasta", f.getHasta().plusDays(1).atStartOfDay());
        if (f.getEstado() != null) {
            q.setParameter("estado", f.getEstado());
        }
        if (f.getTipoComprobante() != null) {
            q.setParameter("tipo", f.getTipoComprobante());
        }
        if (f.getUsuarioId() != null) {
            q.setParameter("usuarioId", f.getUsuarioId());
        }
    }

    private static Map<Integer, Long> prendasPorVenta(EntityManager em, List<Venta> ventas) {
        Map<Integer, Long> prendas = new HashMap<>();
        if (ventas.isEmpty()) {
            return prendas;
        }
        List<Integer> ids = new ArrayList<>();
        for (Venta v : ventas) {
            ids.add(v.getId());
        }
        List<Object[]> filas = em.createQuery(
                "SELECT d.venta.id, SUM(d.cantidad) FROM DetalleVenta d"
                + " WHERE d.venta.id IN :ids GROUP BY d.venta.id", Object[].class)
                .setParameter("ids", ids)
                .getResultList();
        for (Object[] fila : filas) {
            prendas.put(((Number) fila[0]).intValue(), ((Number) fila[1]).longValue());
        }
        return prendas;
    }

    private static ResumenVentas resumen(EntityManager em, FiltroVentas f) {
        ResumenVentas r = new ResumenVentas();
        String desde = " FROM Venta v JOIN v.usuario u LEFT JOIN v.comprobante cp";
        TypedQuery<Object[]> porEstado = em.createQuery(
                "SELECT v.estado, COUNT(v), SUM(v.subtotal), SUM(v.igv), SUM(v.total)"
                + desde + condiciones(f, "") + " GROUP BY v.estado", Object[].class);
        parametros(porEstado, f);
        for (Object[] fila : porEstado.getResultList()) {
            long cantidad = ((Number) fila[1]).longValue();
            if (fila[0] == EstadoVenta.ANULADA) {
                r.setAnuladas(cantidad);
            } else {
                r.setCompletadas(cantidad);
                r.setSubtotal((BigDecimal) fila[2]);
                r.setIgv((BigDecimal) fila[3]);
                r.setTotal((BigDecimal) fila[4]);
            }
        }
        Query prendas = em.createQuery(
                "SELECT SUM(d.cantidad) FROM DetalleVenta d JOIN d.venta v"
                + " JOIN v.usuario u LEFT JOIN v.comprobante cp"
                + condiciones(f, " AND v.estado = :completada"));
        parametros(prendas, f);
        prendas.setParameter("completada", EstadoVenta.COMPLETADA);
        Object totalPrendas = prendas.getSingleResult();   // null si no hay ventas
        r.setPrendas(totalPrendas == null ? 0 : ((Number) totalPrendas).longValue());
        TypedQuery<Object[]> porVendedor = em.createQuery(
                "SELECT u.nombres, u.apellidos, COUNT(v), SUM(v.total)"
                + desde + condiciones(f, " AND v.estado = :completada")
                + " GROUP BY u.id, u.nombres, u.apellidos ORDER BY SUM(v.total) DESC", Object[].class);
        parametros(porVendedor, f);
        porVendedor.setParameter("completada", EstadoVenta.COMPLETADA);
        for (Object[] fila : porVendedor.getResultList()) {
            String nombre = (fila[0] + " " + fila[1]).trim();
            r.getPorVendedor().add(new ResumenVentas.TotalVendedor(nombre,
                    ((Number) fila[2]).longValue(), (BigDecimal) fila[3]));
        }
        return r;
    }

    public static LocalDate inicioDeMes() {
        return LocalDate.now().withDayOfMonth(1);
    }
}
