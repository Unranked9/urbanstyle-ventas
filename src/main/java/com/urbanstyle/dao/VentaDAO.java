package com.urbanstyle.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.urbanstyle.dto.FiltroVentas;
import com.urbanstyle.entity.Venta;

public interface VentaDAO {

    List<Venta> listar(FiltroVentas filtro);

    /** Cantidad de prendas por venta (id venta -> unidades). */
    Map<Integer, Long> contarPrendas(List<Integer> idsVenta);

    Optional<Venta> buscarDetalle(int id);

}
