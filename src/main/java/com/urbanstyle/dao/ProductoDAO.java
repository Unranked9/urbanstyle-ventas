package com.urbanstyle.dao;

import java.util.List;
import java.util.Optional;

import com.urbanstyle.entity.Producto;
import com.urbanstyle.entity.ProductoVariante;

public interface ProductoDAO {

    List<Producto> listar(String texto, Integer categoriaId, Integer marcaId, Boolean activo);

    Optional<Producto> buscarPorIdConVariantes(int id);

    Producto guardar(Producto producto);

    void cambiarEstado(int id, boolean activo);


    List<ProductoVariante> buscarVariantesParaVenta(String texto, int limite);

}
