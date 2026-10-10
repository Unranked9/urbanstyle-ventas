package com.urbanstyle.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.urbanstyle.config.JPAUtil;
import com.urbanstyle.dao.VentaDAO;
import com.urbanstyle.dto.FiltroVentas;
import com.urbanstyle.entity.Venta;

import jakarta.persistence.TypedQuery;

public class VentaDAOImpl implements VentaDAO {

    @Override
    public List<Venta> listar(FiltroVentas f) {
        StringBuilder jpql = new StringBuilder(
                "SELECT v FROM Venta v"
                + " JOIN FETCH v.usuario u"
                + " LEFT JOIN FETCH v.cliente c"
                + " LEFT JOIN FETCH v.comprobante cp"
                + " WHERE 1 = 1");
        if (f.getDesde() != null) {
            jpql.append(" AND v.fecha >= :desde");
        }
        if (f.getHasta() != null) {
            jpql.append(" AND v.fecha < :hasta");
        }
        if (f.getEstado() != null) {
            jpql.append(" AND v.estado = :estado");
        }
        if (f.getTipoComprobante() != null) {
            jpql.append(" AND cp.tipo = :tipo");
        }
        if (f.getUsuarioId() != null) {
            jpql.append(" AND u.id = :usuarioId");
        }
        if (f.getTexto() != null) {
            jpql.append(" AND (LOWER(CONCAT(c.nombres, ' ', c.apellidos)) LIKE :texto"
                    + " OR c.dni LIKE :texto OR cp.serie LIKE :texto OR cp.dniCliente LIKE :texto"
                    + " OR cp.rucCliente LIKE :texto"
                    + (esNumero(f.getTexto()) ? " OR v.id = :numero OR cp.numero = :numero" : "")
                    + ")");
        }
        jpql.append(" ORDER BY v.fecha DESC, v.id DESC");

        return JPAUtil.consultar(em -> {
            TypedQuery<Venta> q = em.createQuery(jpql.toString(), Venta.class);
            if (f.getDesde() != null) {
                q.setParameter("desde", f.getDesde().atStartOfDay());
            }
            if (f.getHasta() != null) {
                q.setParameter("hasta", f.getHasta().plusDays(1).atStartOfDay());
            }
            if (f.getEstado() != null) {
                q.setParameter("estado", f.getEstado());
            }
            if (f.getTipoComprobante() != null) {
                q.setParameter("tipo", f.getTipoComprobante());
            }
            if (f.getUsuarioId() != null) {
                q.setParameter("usuarioId", f.getUsuarioId());
            }
            if (f.getTexto() != null) {
                q.setParameter("texto", "%" + f.getTexto().toLowerCase() + "%");
                if (esNumero(f.getTexto())) {
                    q.setParameter("numero", Integer.valueOf(f.getTexto()));
                }
            }
            return q.setMaxResults(f.getLimite()).getResultList();
        });
    }

    private static boolean esNumero(String texto) {
        return texto != null && texto.matches("\\d{1,9}");
    }

    @Override
    public Map<Integer, Long> contarPrendas(List<Integer> idsVenta) {
        Map<Integer, Long> resultado = new HashMap<>();
        if (idsVenta == null || idsVenta.isEmpty()) {
            return resultado;
        }
        List<Object[]> filas = JPAUtil.consultar(em -> em
                .createQuery("SELECT d.venta.id, SUM(d.cantidad) FROM DetalleVenta d"
                        + " WHERE d.venta.id IN :ids GROUP BY d.venta.id", Object[].class)
                .setParameter("ids", idsVenta)
                .getResultList());
        for (Object[] fila : filas) {
            resultado.put(((Number) fila[0]).intValue(), ((Number) fila[1]).longValue());
        }
        return resultado;
    }

    @Override
    public Optional<Venta> buscarDetalle(int id) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT DISTINCT v FROM Venta v"
                        + " JOIN FETCH v.usuario"
                        + " LEFT JOIN FETCH v.cliente"
                        + " LEFT JOIN FETCH v.comprobante"
                        + " LEFT JOIN FETCH v.detalles d"
                        + " LEFT JOIN FETCH d.variante var"
                        + " LEFT JOIN FETCH var.producto"
                        + " LEFT JOIN FETCH var.color"
                        + " LEFT JOIN FETCH var.talla"
                        + " WHERE v.id = :id", Venta.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst());
    }

}
