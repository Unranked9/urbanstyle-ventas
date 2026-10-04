package com.urbanstyle.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ValidacionesTest {

    @Test
    void nombreValidoNoLanzaExcepcion() {
        assertDoesNotThrow(() -> Validaciones.nombre("Ana", "nombres"));
    }

    @Test
    void nombreVacioLanzaServiceException() {
        assertThrows(ServiceException.class, () -> Validaciones.nombre("", "nombres"));
    }

    @Test
    void nombreDeMasDe60CaracteresLanzaServiceException() {
        String largo = "A".repeat(61);
        assertThrows(ServiceException.class, () -> Validaciones.nombre(largo, "nombres"));
    }

    @Test
    void passwordDeMenosDe6CaracteresLanzaServiceException() {
        assertThrows(ServiceException.class, () -> Validaciones.password("12345"));
    }

    @Test
    void passwordDe6CaracteresEsValida() {
        assertDoesNotThrow(() -> Validaciones.password("abc123"));
    }
}
