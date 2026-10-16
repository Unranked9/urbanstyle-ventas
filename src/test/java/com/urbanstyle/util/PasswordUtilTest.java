package com.urbanstyle.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordUtilTest {

    @Test
    void hashDelUsuarioDePruebaDeLaBD() {
        // Usuario "admi" de sql/01_DB_UrbanStyle.sql (contraseña admi123)
        String hash = "$2a$10$i09ELfmMBDyz1r88F/eKLe36u9E8hulq0g05j7GKtUycK9byIdWqK";
        assertTrue(PasswordUtil.verificar("admi123", hash));
        assertFalse(PasswordUtil.verificar("otra", hash));
    }

    @Test
    void cadaHashUsaUnSaltDistinto() {
        String a = PasswordUtil.hashear("secreto1");
        String b = PasswordUtil.hashear("secreto1");
        assertNotEquals(a, b);
        assertTrue(PasswordUtil.verificar("secreto1", a));
        assertTrue(PasswordUtil.verificar("secreto1", b));
    }

    @Test
    void hashMalFormadoNoCoincide() {
        assertFalse(PasswordUtil.verificar("x", "$2a$10$examplehash4examplehash4"));
        assertFalse(PasswordUtil.verificar("x", null));
        assertFalse(PasswordUtil.verificar(null, "abc"));
    }
}
