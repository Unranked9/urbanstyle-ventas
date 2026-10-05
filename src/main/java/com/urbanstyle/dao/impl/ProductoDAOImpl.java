package com.urbanstyle.dao.impl;

import java.util.List;
import java.util.Optional;

import com.urbanstyle.config.JPAUtil;
import com.urbanstyle.dao.ProductoDAO;
import com.urbanstyle.entity.Producto;
import com.urbanstyle.entity.ProductoVariante;

import jakarta.persistence.TypedQuery;

public class ProductoDAOImpl implements ProductoDAO {

    /**
     * JOIN FETCH trae las variantes en la misma consulta. Sin él, al cerrar
     * el EntityManager la lista "variantes" (LAZY) ya no podría cargarse.
     */
    @Override
    public List<Producto> listar(String texto, Integer categoriaId, Integer marcaId, Boolean activo) {
        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT p FROM Producto p"
                + " JOIN FETCH p.categoria"
                + " LEFT JOIN FETCH p.marca"
                + " LEFT JOIN FETCH p.variantes"
                + " WHERE 1 = 1");
        if (texto != null) {
            jpql.append(" AND (LOWER(p.nombre) LIKE :texto OR LOWER(p.descripcion) LIKE :texto)");
        }
        if (categoriaId != null) {
            jpql.append(" AND p.categoria.id = :categoriaId");
        }
        if (marcaId != null) {
            jpql.append(" AND p.marca.id = :marcaId");
        }
        if (activo != null) {
            jpql.append(" AND p.activo = :activo");
        }
        jpql.append(" ORDER BY p.nombre");

        return JPAUtil.consultar(em -> {
            TypedQuery<Producto> q = em.createQuery(jpql.toString(), Producto.class);
            if (texto != null) {
                q.setParameter("texto", "%" + texto.toLowerCase() + "%");
            }
            if (categoriaId != null) {
                q.setParameter("categoriaId", categoriaId);
            }
            if (marcaId != null) {
                q.setParameter("marcaId", marcaId);
            }
            if (activo != null) {
                q.setParameter("activo", activo);
            }
            return q.getResultList();
        });
    }

    @Override
    public Optional<Producto> buscarPorIdConVariantes(int id) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT DISTINCT p FROM Producto p"
                        + " JOIN FETCH p.categoria"
                        + " LEFT JOIN FETCH p.marca"
                        + " LEFT JOIN FETCH p.variantes"
                        + " WHERE p.id = :id", Producto.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst());
    }

    @Override
    public Producto guardar(Producto producto) {
        return JPAUtil.enTransaccion(em -> {
            if (producto.getId() == null) {
                em.persist(producto);       // cascade PERSIST guarda también las variantes
                return producto;
            }
            return em.merge(producto);      // cascade MERGE actualiza / agrega variantes
        });
    }

    @Override
    public void cambiarEstado(int id, boolean activo) {
        JPAUtil.ejecutarEnTransaccion(em -> {
            Producto p = em.find(Producto.class, id);
            if (p != null) {
                p.setActivo(activo);    // entidad administrada: el UPDATE se hace en el commit
            }
        });
    }

    @Override
    public List<ProductoVariante> buscarVariantesParaVenta(String texto, int limite) {
        String filtro = texto == null ? "" : texto.toLowerCase();
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT v FROM ProductoVariante v"
                        + " JOIN FETCH v.producto p"
                        + " JOIN FETCH p.categoria cat"
                        + " LEFT JOIN FETCH p.marca m"
                        + " JOIN FETCH v.color col"
                        + " JOIN FETCH v.talla t"
                        + " WHERE v.activo = true AND p.activo = true"
                        + " AND (LOWER(p.nombre) LIKE :texto OR LOWER(col.nombre) LIKE :texto"
                        + "      OR LOWER(t.nombre) LIKE :texto OR LOWER(cat.nombre) LIKE :texto"
                        + "      OR LOWER(m.nombre) LIKE :texto)"
                        + " ORDER BY p.nombre, col.nombre, t.id", ProductoVariante.class)
                .setParameter("texto", "%" + filtro + "%")
                .setMaxResults(limite)
                .getResultList());
    }

}
