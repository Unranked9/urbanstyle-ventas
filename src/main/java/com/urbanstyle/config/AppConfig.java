package com.urbanstyle.config;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;

/**
 * Lee los archivos de configuración del classpath (src/main/resources).
 * Las variables de entorno tienen prioridad sobre los archivos.
 */
public final class AppConfig {

    private static final Properties APP = cargar("app.properties", true);
    private static final Properties DB = cargar("db.properties", false);

    private AppConfig() {
    }

    private static Properties cargar(String recurso, boolean obligatorio) {
        Properties props = new Properties();
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream(recurso)) {
            if (in == null) {
                if (obligatorio) {
                    throw new IllegalStateException("No se encontró " + recurso + " en el classpath.");
                }
                return props;
            }
            props.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + recurso, e);
        }
    }

    private static String valor(Properties origen, String clave, String variableEntorno, String porDefecto) {
        String env = variableEntorno == null ? null : System.getenv(variableEntorno);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        String valor = origen.getProperty(clave);
        if (valor != null && !valor.isBlank()) {
            return valor.trim();
        }
        return porDefecto;
    }

    // ---------------- Base de datos ----------------

    public static String dbUrl() {
        return valor(DB, "db.url", "URBANSTYLE_DB_URL", null);
    }

    public static String dbUser() {
        return valor(DB, "db.user", "URBANSTYLE_DB_USER", null);
    }

    public static String dbPassword() {
        String env = System.getenv("URBANSTYLE_DB_PASSWORD");
        if (env != null) {
            return env;
        }
        // La contraseña puede estar vacía (XAMPP usa root sin contraseña).
        return DB.getProperty("db.password", "");
    }

    // ---------------- Negocio ----------------

    public static BigDecimal igvPorcentaje() {
        return new BigDecimal(valor(APP, "venta.igv.porcentaje", null, "18"));
    }

    public static String serieBoleta() {
        return valor(APP, "comprobante.serie.boleta", null, "B001");
    }

    public static String serieFactura() {
        return valor(APP, "comprobante.serie.factura", null, "F001");
    }

    public static int stockBajo() {
        return Integer.parseInt(valor(APP, "producto.stock.bajo", null, "5"));
    }

    public static String uploadsDir() {
        return valor(APP, "uploads.dir", "URBANSTYLE_UPLOADS_DIR", null);
    }
}
