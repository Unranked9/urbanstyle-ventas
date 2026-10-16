package com.urbanstyle.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class TextoTest {

    @Test
    void limpiarEspacios() {
        assertEquals("Polo Cuello Redondo", Texto.limpiar("  Polo   Cuello  Redondo "));
        assertNull(Texto.limpiar("   "));
        assertNull(Texto.limpiar(null));
    }

    @Test
    void documentos() {
        assertTrue(Texto.esDni("70456789"));
        assertFalse(Texto.esDni("7045678"));
        assertTrue(Texto.esRuc("20123456789"));
        assertTrue(Texto.esRuc("10456789012"));
        assertFalse(Texto.esRuc("30123456789"));
        assertFalse(Texto.esRuc("2012345678"));
    }

    @Test
    void numeros() {
        assertEquals(new BigDecimal("45.50"), Texto.decimal("45,50"));
        assertNull(Texto.decimal("abc"));
        assertEquals(7, Texto.entero("x", 7));
    }

    @Test
    void correo() {
        assertTrue(Texto.esEmail("lucia@example.com"));
        assertFalse(Texto.esEmail("lucia@"));
    }
}
