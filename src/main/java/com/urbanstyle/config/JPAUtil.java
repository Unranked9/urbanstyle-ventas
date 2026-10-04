package com.urbanstyle.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

/**
 * Administra el único EntityManagerFactory de la aplicación
 * (reemplaza a la antigua clase DBConexion).
 *
 * - El EntityManagerFactory es costoso: se crea una sola vez al iniciar
 *   (AppStartListener) y se cierra al detener Tomcat.
 * - El EntityManager es liviano: se abre uno por operación y se cierra.
 */
public final class JPAUtil {

    public static final String UNIDAD_PERSISTENCIA = "UrbanStylePU";

    private static volatile EntityManagerFactory emf;

    private JPAUtil() {
    }

    public static synchronized void iniciar() {
        if (emf != null && emf.isOpen()) {
            return;
        }
        String url = AppConfig.dbUrl();
        String user = AppConfig.dbUser();
        if (url == null || user == null) {
            throw new IllegalStateException(
                    "Falta la configuración de la BD. Copia src/main/resources/db.properties.example "
                    + "como db.properties y completa db.url, db.user y db.password.");
        }
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", url);
        props.put("jakarta.persistence.jdbc.user", user);
        props.put("jakarta.persistence.jdbc.password", AppConfig.dbPassword());
        emf = Persistence.createEntityManagerFactory(UNIDAD_PERSISTENCIA, props);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            iniciar();
        }
        return emf;
    }

    public static EntityManager crearEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static synchronized void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }

    /** Ejecuta una consulta de solo lectura y cierra el EntityManager. */
    public static <T> T consultar(Function<EntityManager, T> consulta) {
        EntityManager em = crearEntityManager();
        try {
            return consulta.apply(em);
        } finally {
            em.close();
        }
    }

    /**
     * Ejecuta una operación dentro de una transacción:
     * commit si todo sale bien, rollback si ocurre cualquier excepción.
     */
    public static <T> T enTransaccion(Function<EntityManager, T> operacion) {
        EntityManager em = crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T resultado = operacion.apply(em);
            tx.commit();
            return resultado;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Variante sin valor de retorno de {@link #enTransaccion(Function)}. */
    public static void ejecutarEnTransaccion(Consumer<EntityManager> operacion) {
        enTransaccion(em -> {
            operacion.accept(em);
            return null;
        });
    }
}
