package com.urbanstyle.dto;

import java.math.BigDecimal;

import com.urbanstyle.entity.Cliente;

/**
 * Una fila del reporte de clientes: el cliente y lo que ha comprado
 * (solo ventas completadas).
 */
public class FilaReporteCliente {

    private final Cliente cliente;
    private final long compras;
    private final BigDecimal totalComprado;

    public FilaReporteCliente(Cliente cliente, long compras, BigDecimal totalComprado) {
        this.cliente = cliente;
        this.compras = compras;
        this.totalComprado = totalComprado == null ? BigDecimal.ZERO : totalComprado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public long getCompras() {
        return compras;
    }

    public BigDecimal getTotalComprado() {
        return totalComprado;
    }
}
