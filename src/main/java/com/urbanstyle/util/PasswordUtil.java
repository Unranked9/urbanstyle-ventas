package com.urbanstyle.util;

import org.mindrot.jbcrypt.BCrypt;


public final class PasswordUtil {

    private static final int COSTO = 10;

    private PasswordUtil() {
    }

    public static String hashear(String textoPlano) {
        if (textoPlano == null || textoPlano.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        return BCrypt.hashpw(textoPlano, BCrypt.gensalt(COSTO));
    }

    public static boolean verificar(String textoPlano, String hashGuardado) {
        if (textoPlano == null || hashGuardado == null || !hashGuardado.startsWith("$2")) {
            return false;
        }
        try {
            return BCrypt.checkpw(textoPlano, hashGuardado);
        } catch (IllegalArgumentException e) {
            // Hash mal formado (por ejemplo, datos de prueba): no coincide.
            return false;
        }
    }
}
