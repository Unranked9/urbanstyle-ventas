package com.urbanstyle.dao.impl;

import java.util.List;
import java.util.Optional;

import com.urbanstyle.config.JPAUtil;
import com.urbanstyle.dao.ClienteDAO;
import com.urbanstyle.entity.Cliente;

import jakarta.persistence.TypedQuery;

public class ClienteDAOImpl implements ClienteDAO {

    private static final String CONDICION_TEXTO =
            "(LOWER(c.nombres) LIKE :texto OR LOWER(c.apellidos) LIKE :texto"
            + " OR LOWER(CONCAT(c.nombres, ' ', c.apellidos)) LIKE :texto"
            + " OR c.dni LIKE :texto OR LOWER(c.email) LIKE :texto OR c.telefono LIKE :texto)";

    @Override
    public List<Cliente> listar(String texto, Boolean activo, Boolean mayorista) {
        StringBuilder jpql = new StringBuilder("SELECT c FROM Cliente c WHERE 1 = 1");
        if (texto != null) {
            jpql.append(" AND ").append(CONDICION_TEXTO);
        }
        if (activo != null) {
            jpql.append(" AND c.activo = :activo");
        }
        if (mayorista != null) {
            jpql.append(" AND c.mayorista = :mayorista");
        }
        jpql.append(" ORDER BY c.activo DESC, c.nombres, c.apellidos");

        return JPAUtil.consultar(em -> {
            TypedQuery<Cliente> q = em.createQuery(jpql.toString(), Cliente.class);
            if (texto != null) {
                q.setParameter("texto", "%" + texto.toLowerCase() + "%");
            }
            if (activo != null) {
                q.setParameter("activo", activo);
            }
            if (mayorista != null) {
                q.setParameter("mayorista", mayorista);
            }
            return q.getResultList();
        });
    }

    @Override
    public List<Cliente> buscarActivos(String texto, int limite) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT c FROM Cliente c WHERE c.activo = true AND " + CONDICION_TEXTO
                        + " ORDER BY c.nombres, c.apellidos", Cliente.class)
                .setParameter("texto", "%" + (texto == null ? "" : texto.toLowerCase()) + "%")
                .setMaxResults(limite)
                .getResultList());
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return JPAUtil.consultar(em -> Optional.ofNullable(em.find(Cliente.class, id)));
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return JPAUtil.enTransaccion(em -> {
            if (cliente.getId() == null) {
                em.persist(cliente);
                return cliente;
            }
            return em.merge(cliente);
        });
    }

    @Override
    public boolean existeEmail(String email, Integer excluirId) {
        return existe("LOWER(c.email) = LOWER(:valor)", email, excluirId);
    }

    @Override
    public boolean existeDni(String dni, Integer excluirId) {
        return existe("c.dni = :valor", dni, excluirId);
    }

    @Override
    public boolean existeUsername(String username, Integer excluirId) {
        return existe("LOWER(c.username) = LOWER(:valor)", username, excluirId);
    }

    private boolean existe(String condicion, String valor, Integer excluirId) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT COUNT(c) FROM Cliente c WHERE " + condicion
                        + " AND (:excluir IS NULL OR c.id <> :excluir)", Long.class)
                .setParameter("valor", valor)
                .setParameter("excluir", excluirId)
                .getSingleResult() > 0);
    }

}
