package com.urbanstyle.config;

import java.nio.file.Path;

import com.urbanstyle.util.ImagenUtil;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Inicia JPA al desplegar la aplicación y lo cierra al detenerla.
 * Si la BD no responde, la aplicación no arranca (falla rápido y
 * deja el motivo en la consola de Tomcat).
 */
@WebListener
public class AppStartListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        var context = event.getServletContext();
        try {
            JPAUtil.iniciar();
            // Consulta mínima para comprobar conexión y mapeo.
            Long roles = JPAUtil.consultar(em ->
                    em.createQuery("SELECT COUNT(r) FROM Rol r", Long.class).getSingleResult());
            Path uploads = ImagenUtil.carpetaUploads();
            context.log("[URBANSTYLE] JPA iniciado. Roles en BD: " + roles
                    + " | Imágenes en: " + uploads);
        } catch (RuntimeException e) {
            context.log("[URBANSTYLE][INICIO_FALLIDO] " + e.getMessage(), e);
            throw new IllegalStateException(
                    "UrbanStyle no pudo iniciar. Revisa db.properties y que DB_UrbanStyle exista.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        JPAUtil.cerrar();
        event.getServletContext().log("[URBANSTYLE] Aplicación detenida.");
    }
}
