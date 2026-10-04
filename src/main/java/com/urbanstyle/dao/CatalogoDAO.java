package com.urbanstyle.dao;

import java.util.List;
import java.util.Optional;

/**
 * Operaciones comunes de las tablas maestras sencillas
 * (categories, brands, colors, sizes).
 */
public interface CatalogoDAO<T> {

    List<T> listar();

    Optional<T> buscarPorId(int id);

    T guardar(T entidad);

    void eliminar(int id);

    boolean existeNombre(String nombre, Integer excluirId);

    /** true si algún producto o variante lo usa (no se puede eliminar). */
    boolean estaEnUso(int id);
}
