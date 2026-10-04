package com.urbanstyle.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.urbanstyle.config.AppConfig;

import jakarta.servlet.http.Part;

/**
 * Guarda las imágenes de las variantes en una carpeta EXTERNA al proyecto.
 *
 * La versión anterior escribía dentro de src/main/webapp del workspace de
 * Eclipse, lo que no funciona al desplegar un .war generado por Maven.
 * Ahora la carpeta se define en app.properties (uploads.dir) o con la
 * variable de entorno URBANSTYLE_UPLOADS_DIR, y ImagenController sirve
 * los archivos en /uploads/{archivo}.
 */
public final class ImagenUtil {

    public static final long TAMANO_MAXIMO = 3L * 1024 * 1024; // 3 MB

    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif");

    private static volatile Path carpeta;

    private ImagenUtil() {
    }

    public static synchronized Path carpetaUploads() {
        if (carpeta != null) {
            return carpeta;
        }
        String configurada = AppConfig.uploadsDir();
        Path ruta = configurada == null
                ? Path.of(System.getProperty("user.home"), "urbanstyle-uploads")
                : Path.of(configurada);
        try {
            Files.createDirectories(ruta);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear la carpeta de imágenes: " + ruta, e);
        }
        carpeta = ruta.toAbsolutePath().normalize();
        return carpeta;
    }

    public static boolean tieneArchivo(Part part) {
        return part != null && part.getSize() > 0
                && part.getSubmittedFileName() != null && !part.getSubmittedFileName().isBlank();
    }

    /**
     * Valida y guarda la imagen. Devuelve la URL relativa que se guarda en
     * product_variants.image_url (por ejemplo "uploads/3f2a...webp").
     */
    public static String guardar(Part part) throws IOException {
        if (!tieneArchivo(part)) {
            return null;
        }
        if (part.getSize() > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("La imagen supera el tamaño máximo de 3 MB.");
        }
        String tipo = part.getContentType() == null ? "" : part.getContentType().toLowerCase(Locale.ROOT);
        String extension = EXTENSIONES.get(tipo);
        if (extension == null) {
            throw new IllegalArgumentException("Formato de imagen no permitido. Use JPG, PNG, WEBP o GIF.");
        }
        String nombre = UUID.randomUUID().toString().replace("-", "") + extension;
        Path destino = carpetaUploads().resolve(nombre);
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
        }
        return "uploads/" + nombre;
    }

    /** Resuelve un nombre de archivo evitando rutas como "../../etc/passwd". */
    public static Path resolver(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.matches("[A-Za-z0-9_.-]+")) {
            return null;
        }
        Path ruta = carpetaUploads().resolve(nombreArchivo).normalize();
        return ruta.startsWith(carpetaUploads()) ? ruta : null;
    }

    public static String tipoContenido(Path ruta) {
        String nombre = ruta.getFileName().toString().toLowerCase(Locale.ROOT);
        for (Map.Entry<String, String> e : EXTENSIONES.entrySet()) {
            if (nombre.endsWith(e.getValue())) {
                return e.getKey();
            }
        }
        return "application/octet-stream";
    }
}
