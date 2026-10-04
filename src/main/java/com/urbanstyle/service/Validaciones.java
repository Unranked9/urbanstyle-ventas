package com.urbanstyle.service;

/**
 * Validaciones compartidas por varios servicios (usuarios y clientes).
 * Lanzan ServiceException con un mensaje listo para mostrar al usuario.
 */
public final class Validaciones {

    private Validaciones() {
    }

    /** Nombres y apellidos: obligatorios, máximo 60 caracteres (first_name / last_name). */
    public static void nombre(String valor, String campo) {
        if (valor == null || valor.length() > 60) {
            throw new ServiceException("Los " + campo + " son obligatorios (máximo 60 caracteres).");
        }
    }

    /** Contraseñas: entre 6 y 72 caracteres (72 es el máximo que admite BCrypt). */
    public static void password(String password) {
        if (password == null || password.length() < 6 || password.length() > 72) {
            throw new ServiceException("La contraseña debe tener entre 6 y 72 caracteres.");
        }
    }
}
