package com.urbanstyle.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Cálculos de dinero de una venta. Es una clase "pura" (sin BD) para
 * poder probarla con JUnit: ver CalculadoraVentaTest.
 *
 * Regla heredada de la versión anterior: el IGV se suma sobre el subtotal.
 *   subtotal = suma de (precio x cantidad)
 *   igv      = subtotal x porcentaje / 100
 *   total    = subtotal + igv
 */
public final class CalculadoraVenta {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private CalculadoraVenta() {
    }

    public record Totales(BigDecimal subtotal, BigDecimal igv, BigDecimal total) {
    }

    public static BigDecimal dinero(BigDecimal valor) {
        return (valor == null ? BigDecimal.ZERO : valor).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal totalLinea(BigDecimal precioUnitario, int cantidad) {
        if (precioUnitario == null || precioUnitario.signum() < 0) {
            throw new IllegalArgumentException("Precio unitario inválido.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        return dinero(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
    }

    public static Totales calcular(List<BigDecimal> totalesLinea, BigDecimal porcentajeIgv) {
        if (porcentajeIgv == null || porcentajeIgv.signum() < 0) {
            throw new IllegalArgumentException("Porcentaje de IGV inválido.");
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        for (BigDecimal linea : totalesLinea) {
            subtotal = subtotal.add(dinero(linea));
        }
        subtotal = dinero(subtotal);
        BigDecimal igv = dinero(subtotal.multiply(porcentajeIgv).divide(CIEN, 8, RoundingMode.HALF_UP));
        return new Totales(subtotal, igv, dinero(subtotal.add(igv)));
    }
}
