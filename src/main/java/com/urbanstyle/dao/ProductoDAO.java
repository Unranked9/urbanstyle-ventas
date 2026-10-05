package com.urbanstyle.dao;

import java.util.List;
import java.util.Optional;

import com.urbanstyle.entity.Producto;
import com.urbanstyle.entity.ProductoVariante;

public interface ProductoDAO {

    /** Lista productos con sus variantes (JOIN FETCH). Los filtros nulos se ignoran. */
    List<Producto> listar(String texto, Integer categoriaId, Integer marcaId, Boolean activo);

    Optional<Producto> buscarPorIdConVariantes(int id);

    Producto guardar(Producto producto);

    void cambiarEstado(int id, boolean activo);

    /** Variantes activas de productos activos que coinciden con el texto. */
    List<ProductoVariante> buscarVariantesParaVenta(String texto, int limite);

}
