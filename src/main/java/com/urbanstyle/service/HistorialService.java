package com.urbanstyle.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.urbanstyle.config.JPAUtil;
import com.urbanstyle.dao.VentaDAO;
import com.urbanstyle.dao.impl.VentaDAOImpl;
import com.urbanstyle.dto.FiltroVentas;
import com.urbanstyle.entity.DetalleVenta;
import com.urbanstyle.entity.EstadoVenta;
import com.urbanstyle.entity.ProductoVariante;
import com.urbanstyle.entity.Venta;

import jakarta.persistence.LockModeType;

/**
 * Consultas de ventas ya registradas y su anulación.
 * (El registro de ventas nuevas está en VentaService.)
 */
public class HistorialService {

    private final VentaDAO ventaDAO;

    public HistorialService() {
        this(new VentaDAOImpl());
    }

    public HistorialService(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }

    public List<Venta> listar(FiltroVentas filtro) {
        return ventaDAO.listar(filtro);
    }

    /** Cantidad de prendas de cada venta (id venta -> unidades). */
    public Map<Integer, Long> contarPrendas(List<Venta> ventas) {
        List<Integer> ids = new ArrayList<>();
        for (Venta v : ventas) {
            ids.add(v.getId());
        }
        return ventaDAO.contarPrendas(ids);
    }

    public Venta buscarDetalle(int id) {
        return ventaDAO.buscarDetalle(id)
                .orElseThrow(() -> new ServiceException("La venta #" + id + " no existe."));
    }

    /**
     * Marca la venta como anulada y devuelve el stock vendido, todo en una
     * transacción. La venta y las variantes se bloquean (SELECT ... FOR UPDATE)
     * para que otra caja no las modifique al mismo tiempo.
     */
    public void anular(int ventaId) {
        JPAUtil.ejecutarEnTransaccion(em -> {
            Venta venta = em.find(Venta.class, ventaId, LockModeType.PESSIMISTIC_WRITE);
            if (venta == null) {
                throw new ServiceException("La venta #" + ventaId + " no existe.");
            }
            if (venta.isAnulada()) {
                throw new ServiceException("La venta #" + ventaId + " ya estaba anulada.");
            }
            venta.setEstado(EstadoVenta.ANULADA);
            // Agrupar por variante (por si una variante aparece en más de un detalle).
            Map<Integer, Integer> devolver = new TreeMap<>();
            Map<Integer, ProductoVariante> variantes = new TreeMap<>();
            for (DetalleVenta detalle : venta.getDetalles()) {
                ProductoVariante variante = detalle.getVariante();
                devolver.merge(variante.getId(), detalle.getCantidad(), Integer::sum);
                variantes.put(variante.getId(), variante);
            }
            for (Map.Entry<Integer, Integer> e : devolver.entrySet()) {
                ProductoVariante variante = variantes.get(e.getKey());
                // refresh + bloqueo: relee el stock actual con SELECT ... FOR UPDATE
                em.refresh(variante, LockModeType.PESSIMISTIC_WRITE);
                variante.setStock(variante.getStock() + e.getValue());
            }
        });
    }
}
