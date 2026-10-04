package com.urbanstyle.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lectura y escritura de JSON para los endpoints AJAX (/api/...).
 */
public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private JsonUtil() {
    }

    public static void responder(HttpServletResponse res, int status, Object cuerpo) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("Cache-Control", "no-store");
        res.getWriter().write(GSON.toJson(cuerpo));
    }

    public static void ok(HttpServletResponse res, Object cuerpo) throws IOException {
        responder(res, HttpServletResponse.SC_OK, cuerpo);
    }

    public static void error(HttpServletResponse res, int status, String mensaje) throws IOException {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("ok", false);
        cuerpo.put("mensaje", mensaje);
        responder(res, status, cuerpo);
    }

    public static <T> T leer(HttpServletRequest req, Class<T> tipo) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }
        }
        try {
            return GSON.fromJson(sb.toString(), tipo);
        } catch (JsonParseException e) {
            return null;
        }
    }

    public static String aJson(Object objeto) {
        return GSON.toJson(objeto);
    }
}
