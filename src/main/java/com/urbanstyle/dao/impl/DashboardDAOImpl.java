package com.urbanstyle.dao.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.urbanstyle.config.JPAUtil;
import com.urbanstyle.dao.DashboardDAO;
import com.urbanstyle.dto.ProductoVendido;
import com.urbanstyle.entity.EstadoVenta;
import com.urbanstyle.entity.ProductoVariante;
import com.urbanstyle.entity.Venta;

public class DashboardDAOImpl implements DashboardDAO {

    @Override
    public BigDecimal totalVendido(LocalDateTime desde, LocalDateTime hasta) {
        BigDecimal suma = JPAUtil.consultar(em -> em
                .createQuery("SELECT SUM(v.total) FROM Venta v"
                        + " WHERE v.estado = :estado AND v.fecha >= :desde AND v.fecha < :hasta",
                        BigDecimal.class)
                .setParameter("estado", EstadoVenta.COMPLETADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getSingleResult());
        // SUM devuelve NULL cuando no hay filas.
        return suma == null ? BigDecimal.ZERO : suma;
    }

    @Override
    public long contarVentas(LocalDateTime desde, LocalDateTime hasta) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT COUNT(v) FROM Venta v"
                        + " WHERE v.estado = :estado AND v.fecha >= :desde AND v.fecha < :hasta",
                        Long.class)
                .setParameter("estado", EstadoVenta.COMPLETADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getSingleResult());
    }

    @Override
    public List<Object[]> fechasYTotales(LocalDateTime desde, LocalDateTime hasta) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT v.fecha, v.total FROM Venta v"
                        + " WHERE v.estado = :estado AND v.fecha >= :desde AND v.fecha < :hasta",
                        Object[].class)
                .setParameter("estado", EstadoVenta.COMPLETADA)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList());
    }

    /** Ejemplo de expresión constructora de JPQL: SELECT NEW ... con GROUP BY. */
    @Override
    public List<ProductoVendido> topProductos(LocalDateTime desde, int limite) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT NEW com.urbanstyle.dto.ProductoVendido("
                        + "   p.nombre, SUM(d.cantidad), SUM(d.totalLinea))"
                        + " FROM DetalleVenta d JOIN d.variante var JOIN var.producto p JOIN d.venta v"
                        + " WHERE v.estado = :estado AND v.fecha >= :desde"
                        + " GROUP BY p.id, p.nombre"
                        + " ORDER BY SUM(d.cantidad) DESC", ProductoVendido.class)
                .setParameter("estado", EstadoVenta.COMPLETADA)
                .setParameter("desde", desde)
                .setMaxResults(limite)
                .getResultList());
    }

    @Override
    public List<Venta> ultimasVentas(int limite) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT v FROM Venta v JOIN FETCH v.usuario LEFT JOIN FETCH v.cliente"
                        + " LEFT JOIN FETCH v.comprobante ORDER BY v.fecha DESC, v.id DESC", Venta.class)
                .setMaxResults(limite)
                .getResultList());
    }

    @Override
    public long contarProductosActivos() {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT COUNT(p) FROM Producto p WHERE p.activo = true", Long.class)
                .getSingleResult());
    }

    @Override
    public long contarClientesActivos() {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT COUNT(c) FROM Cliente c WHERE c.activo = true", Long.class)
                .getSingleResult());
    }

    @Override
    public List<ProductoVariante> listarStockBajo(int umbral, int limite) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT v FROM ProductoVariante v"
                        + " JOIN FETCH v.producto p JOIN FETCH v.color JOIN FETCH v.talla"
                        + " WHERE v.activo = true AND p.activo = true AND v.stock <= :umbral"
                        + " ORDER BY v.stock, p.nombre", ProductoVariante.class)
                .setParameter("umbral", umbral)
                .setMaxResults(limite)
                .getResultList());
    }

    @Override
    public long contarStockBajo(int umbral) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT COUNT(v) FROM ProductoVariante v"
                        + " WHERE v.activo = true AND v.producto.activo = true AND v.stock <= :umbral",
                        Long.class)
                .setParameter("umbral", umbral)
                .getSingleResult());
    }
}
