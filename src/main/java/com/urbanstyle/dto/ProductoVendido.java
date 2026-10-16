package com.urbanstyle.dto;

import java.math.BigDecimal;

/**
 * Resultado de una consulta JPQL con expresión constructora:
 * SELECT NEW com.urbanstyle.dto.ProductoVendido(p.nombre, SUM(...), SUM(...)) ...
 */
public class ProductoVendido {

    private final String producto;
    private final long unidades;
    private final BigDecimal monto;

    public ProductoVendido(String producto, Long unidades, BigDecimal monto) {
        this.producto = producto;
        this.unidades = unidades == null ? 0 : unidades;
        this.monto = monto == null ? BigDecimal.ZERO : monto;
    }

    public String getProducto() {
        return producto;
    }

    public long getUnidades() {
        return unidades;
    }

    public BigDecimal getMonto() {
        return monto;
    }
}
