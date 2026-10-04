package com.urbanstyle.dao;

import java.util.List;
import java.util.Optional;

import com.urbanstyle.entity.Cliente;

public interface ClienteDAO {

    List<Cliente> listar(String texto, Boolean activo);

    /** Búsqueda rápida para la pantalla de venta (por nombre, DNI o email). */
    List<Cliente> buscarActivos(String texto, int limite);

    Optional<Cliente> buscarPorId(int id);

    Cliente guardar(Cliente cliente);

    boolean existeEmail(String email, Integer excluirId);

    boolean existeDni(String dni, Integer excluirId);

    boolean existeUsername(String username, Integer excluirId);

}
