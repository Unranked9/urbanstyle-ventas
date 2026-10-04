package com.urbanstyle.dao.impl;

import java.util.List;
import java.util.Optional;

import com.urbanstyle.config.JPAUtil;
import com.urbanstyle.dao.UsuarioDAO;
import com.urbanstyle.entity.Rol;
import com.urbanstyle.entity.Usuario;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.username = :username",
                        Usuario.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst());
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        return JPAUtil.consultar(em -> Optional.ofNullable(em.find(Usuario.class, id)));
    }

    @Override
    public List<Usuario> listar() {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT u FROM Usuario u JOIN FETCH u.rol ORDER BY u.activo DESC, u.nombres",
                        Usuario.class)
                .getResultList());
    }

    @Override
    public List<Usuario> listarActivos() {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.activo = true ORDER BY u.nombres",
                        Usuario.class)
                .getResultList());
    }

    @Override
    public List<Rol> listarRoles() {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT r FROM Rol r ORDER BY r.id", Rol.class)
                .getResultList());
    }

    @Override
    public Optional<Rol> buscarRol(int id) {
        return JPAUtil.consultar(em -> Optional.ofNullable(em.find(Rol.class, id)));
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return JPAUtil.enTransaccion(em -> {
            if (usuario.getId() == null) {
                em.persist(usuario);   // INSERT
                return usuario;
            }
            return em.merge(usuario);  // UPDATE
        });
    }

    @Override
    public boolean existeUsername(String username, Integer excluirId) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT COUNT(u) FROM Usuario u WHERE LOWER(u.username) = LOWER(:valor)"
                        + " AND (:excluir IS NULL OR u.id <> :excluir)", Long.class)
                .setParameter("valor", username)
                .setParameter("excluir", excluirId)
                .getSingleResult() > 0);
    }

    @Override
    public boolean existeDni(String dni, Integer excluirId) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.dni = :valor"
                        + " AND (:excluir IS NULL OR u.id <> :excluir)", Long.class)
                .setParameter("valor", dni)
                .setParameter("excluir", excluirId)
                .getSingleResult() > 0);
    }

    @Override
    public long contarAdministradoresActivos() {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.activo = true AND u.rol.nombre = :rol",
                        Long.class)
                .setParameter("rol", Rol.ADMIN)
                .getSingleResult());
    }
}
