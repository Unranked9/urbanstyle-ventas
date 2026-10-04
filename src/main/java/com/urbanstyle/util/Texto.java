package com.urbanstyle.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Utilidades para leer y validar datos que llegan de formularios.
 */
public final class Texto {

    private Texto() {
    }

    /** Recorta espacios; devuelve null si queda vacío. */
    public static String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String v = valor.trim().replaceAll("\\s+", " ");
        return v.isEmpty() ? null : v;
    }

    public static boolean vacio(String valor) {
        return limpiar(valor) == null;
    }

    public static Integer entero(String valor) {
        String v = limpiar(valor);
        if (v == null) {
            return null;
        }
        try {
            return Integer.valueOf(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int entero(String valor, int porDefecto) {
        Integer v = entero(valor);
        return v == null ? porDefecto : v;
    }

    public static BigDecimal decimal(String valor) {
        String v = limpiar(valor);
        if (v == null) {
            return null;
        }
        try {
            return new BigDecimal(v.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static LocalDate fecha(String valor) {
        String v = limpiar(valor);
        if (v == null) {
            return null;
        }
        try {
            return LocalDate.parse(v);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static boolean esDni(String valor) {
        return valor != null && valor.matches("\\d{8}");
    }

    public static boolean esRuc(String valor) {
        return valor != null && valor.matches("(10|15|17|20)\\d{9}");
    }

    public static boolean esEmail(String valor) {
        return valor != null && valor.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    }

    public static boolean esTelefono(String valor) {
        return valor != null && valor.matches("[0-9+ ]{6,15}");
    }

    /** Escapa texto para insertarlo en HTML generado desde Java. */
    public static String html(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
