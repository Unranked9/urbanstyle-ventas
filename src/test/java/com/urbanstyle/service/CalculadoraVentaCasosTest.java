package com.urbanstyle.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class CalculadoraVentaCasosTest {

    @Test
    void testVentaTresLineas() {
        // Prueba 1: Venta con 3 líneas sumadas (100.00 + 30.00 + 30.00 = 160.00)
        BigDecimal l1 = CalculadoraVenta.totalLinea(new BigDecimal("50.00"), 2);
        BigDecimal l2 = CalculadoraVenta.totalLinea(new BigDecimal("30.00"), 1);
        BigDecimal l3 = CalculadoraVenta.totalLinea(new BigDecimal("10.00"), 3);
        
        BigDecimal total = l1.add(l2).add(l3);
        assertEquals(new BigDecimal("160.00"), total);
    }

    @Test
    void testRedondeoCentimos() {
        // Prueba 2: Precio con céntimos que obliga a redondear
        BigDecimal total = CalculadoraVenta.totalLinea(new BigDecimal("19.99"), 3);
        assertEquals(new BigDecimal("59.97"), total);
    }

    @Test
    void testCantidadCeroLanzaExcepcion() {
        // Prueba 3: Cantidad cero debe lanzar excepción
        assertThrows(Exception.class, () -> {
            CalculadoraVenta.totalLinea(new BigDecimal("50.00"), 0);
        });
    }

    @Test
    void testCasoLimiteBoleta700() {
        // Prueba 4: Caso límite boleta de S/ 700
        BigDecimal total = CalculadoraVenta.totalLinea(new BigDecimal("700.00"), 1);
        assertEquals(new BigDecimal("700.00"), total);
    }
}
