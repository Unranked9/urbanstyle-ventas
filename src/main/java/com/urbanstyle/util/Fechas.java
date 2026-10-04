package com.urbanstyle.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formato de fechas para las vistas. (fmt:formatDate de JSTL no admite
 * java.time.LocalDateTime, por eso las entidades exponen getters "...Texto".)
 */
public final class Fechas {

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Fechas() {
    }

    public static String fechaHora(LocalDateTime valor) {
        return valor == null ? "" : FECHA_HORA.format(valor);
    }

    public static String fecha(LocalDateTime valor) {
        return valor == null ? "" : FECHA.format(valor);
    }
}
