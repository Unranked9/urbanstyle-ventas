package com.urbanstyle.controller;

import java.io.IOException;

import com.urbanstyle.dto.UsuarioSesion;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Utilidades comunes de los controladores: vistas, redirecciones,
 * mensajes "flash" (se muestran una sola vez) y usuario de la sesión.
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String SESION_USUARIO = "usuarioSesion";
    private static final String FLASH_OK = "flashOk";
    private static final String FLASH_ERROR = "flashError";

    protected UsuarioSesion usuario(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (UsuarioSesion) s.getAttribute(SESION_USUARIO);
    }

    /** Muestra /WEB-INF/views/{vista}.jsp con el título y la opción de menú activa. */
    protected void vista(HttpServletRequest req, HttpServletResponse res, String vista,
                         String titulo, String menu) throws ServletException, IOException {
        req.setAttribute("tituloPagina", titulo);
        req.setAttribute("paginaActual", menu);
        // Pasar los mensajes flash a la petición y retirarlos de la sesión.
        HttpSession s = req.getSession(false);
        if (s != null) {
            moverFlash(s, req, FLASH_OK);
            moverFlash(s, req, FLASH_ERROR);
        }
        req.getRequestDispatcher("/WEB-INF/views/" + vista + ".jsp").forward(req, res);
    }

    private void moverFlash(HttpSession s, HttpServletRequest req, String clave) {
        Object valor = s.getAttribute(clave);
        if (valor != null) {
            req.setAttribute(clave, valor);
            s.removeAttribute(clave);
        }
    }

    protected void redirigir(HttpServletRequest req, HttpServletResponse res, String ruta) throws IOException {
        res.sendRedirect(req.getContextPath() + ruta);
    }

    protected void ok(HttpServletRequest req, String mensaje) {
        req.getSession().setAttribute(FLASH_OK, mensaje);
    }

    protected void error(HttpServletRequest req, String mensaje) {
        req.getSession().setAttribute(FLASH_ERROR, mensaje);
    }

    protected boolean esPost(HttpServletRequest req) {
        return "POST".equalsIgnoreCase(req.getMethod());
    }

    /** Mensaje legible para errores inesperados (no expone detalles técnicos). */
    protected String mensajeInesperado(Exception e) {
        log("Error inesperado", e);
        return "Ocurrió un error inesperado. Revisa la consola del servidor.";
    }
}
