package com.urbanstyle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias del cálculo de ventas.
 * Se ejecutan con "mvn test" (fase test del ciclo de vida de Maven).
 */
class CalculadoraVentaTest {

    private static final BigDecimal IGV_18 = new BigDecimal("18");

    @Test
    @DisplayName("Total de línea = precio x cantidad con 2 decimales")
    void totalLinea() {
        assertEquals(new BigDecimal("135.00"), CalculadoraVenta.totalLinea(new BigDecimal("45.00"), 3));
        assertEquals(new BigDecimal("119.70"), CalculadoraVenta.totalLinea(new BigDecimal("39.90"), 3));
    }

    @Test
    @DisplayName("IGV 18% se suma sobre el subtotal")
    void igvSobreSubtotal() {
        CalculadoraVenta.Totales t = CalculadoraVenta.calcular(
                List.of(new BigDecimal("135.00")), IGV_18);
        assertEquals(new BigDecimal("135.00"), t.subtotal());
        assertEquals(new BigDecimal("24.30"), t.igv());
        assertEquals(new BigDecimal("159.30"), t.total());
    }

    @Test
    @DisplayName("Redondeo HALF_UP del IGV")
    void redondeo() {
        // 39.90 x 18% = 7.182 -> 7.18
        CalculadoraVenta.Totales t = CalculadoraVenta.calcular(List.of(new BigDecimal("39.90")), IGV_18);
        assertEquals(new BigDecimal("7.18"), t.igv());
        assertEquals(new BigDecimal("47.08"), t.total());
        // 0.25 x 18% = 0.045 -> 0.05
        t = CalculadoraVenta.calcular(List.of(new BigDecimal("0.25")), IGV_18);
        assertEquals(new BigDecimal("0.05"), t.igv());
    }

    @Test
    @DisplayName("Varias líneas se suman antes de calcular el IGV")
    void variasLineas() {
        CalculadoraVenta.Totales t = CalculadoraVenta.calcular(
                List.of(new BigDecimal("90.00"), new BigDecimal("380.00"), new BigDecimal("55.00")), IGV_18);
        assertEquals(new BigDecimal("525.00"), t.subtotal());
        assertEquals(new BigDecimal("94.50"), t.igv());
        assertEquals(new BigDecimal("619.50"), t.total());
    }

    @Test
    @DisplayName("Sin líneas el total es cero")
    void sinLineas() {
        CalculadoraVenta.Totales t = CalculadoraVenta.calcular(List.of(), IGV_18);
        assertEquals(new BigDecimal("0.00"), t.total());
    }

    @Test
    @DisplayName("Cantidades y precios inválidos lanzan excepción")
    void invalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> CalculadoraVenta.totalLinea(new BigDecimal("10.00"), 0));
        assertThrows(IllegalArgumentException.class,
                () -> CalculadoraVenta.totalLinea(new BigDecimal("-1.00"), 1));
        assertThrows(IllegalArgumentException.class,
                () -> CalculadoraVenta.calcular(List.of(BigDecimal.ONE), new BigDecimal("-18")));
    }
}
