package com.urbanstyle.dao;

import java.util.List;
import java.util.Optional;

import com.urbanstyle.entity.Rol;
import com.urbanstyle.entity.Usuario;

public interface UsuarioDAO {

    Optional<Usuario> buscarPorUsername(String username);

    Optional<Usuario> buscarPorId(int id);

    List<Usuario> listar();

    List<Usuario> listarActivos();

    List<Rol> listarRoles();

    Optional<Rol> buscarRol(int id);

    Usuario guardar(Usuario usuario);

    boolean existeUsername(String username, Integer excluirId);

    boolean existeDni(String dni, Integer excluirId);

    long contarAdministradoresActivos();
}
