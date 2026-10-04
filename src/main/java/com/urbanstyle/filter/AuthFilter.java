package com.urbanstyle.filter;

import java.io.IOException;
import java.util.Map;

import com.urbanstyle.controller.BaseServlet;
import com.urbanstyle.dto.UsuarioSesion;
import com.urbanstyle.entity.Rol;
import com.urbanstyle.service.AuthService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Filtro único de seguridad (con anotaciones el orden entre varios
 * filtros no está garantizado, por eso autenticación y permisos van juntos).
 *
 * 1. Rutas públicas: /login y recursos estáticos.
 * 2. Resto: requiere sesión iniciada.
 * 3. Algunas rutas exigen un rol específico.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    /** Cada cuánto se verifica en BD que el usuario siga activo. */
    private static final long REVALIDAR_MS = 60_000;
    private static final String ULTIMA_VALIDACION = "ultimaValidacion";

    private static final String[] SOLO_ADMIN = {Rol.ADMIN};
    private static final String[] GESTORES = {Rol.ADMIN, Rol.SUB_ADMIN};

    /** Ruta (o prefijo terminado en /) -> roles permitidos. */
    private static final Map<String, String[]> PERMISOS = Map.of(
            "/usuarios", SOLO_ADMIN,
            "/usuarios/", SOLO_ADMIN,
            "/catalogos", GESTORES,
            "/catalogos/", GESTORES,
            "/productos/nuevo", GESTORES,
            "/productos/editar", GESTORES,
            "/productos/guardar", GESTORES,
            "/productos/estado", GESTORES,
            "/ventas/anular", GESTORES);

    private final AuthService authService = new AuthService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");

        String ruta = req.getRequestURI().substring(req.getContextPath().length());
        if (esPublica(ruta)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession sesion = req.getSession(false);
        UsuarioSesion usuario = sesion == null ? null
                : (UsuarioSesion) sesion.getAttribute(BaseServlet.SESION_USUARIO);

        if (usuario != null && !sigueActivo(sesion, usuario)) {
            sesion.invalidate();
            usuario = null;
        }
        if (usuario == null) {
            if (ruta.startsWith("/api/")) {
                responderJson(res, HttpServletResponse.SC_UNAUTHORIZED, "Tu sesión expiró. Vuelve a iniciar sesión.");
            } else {
                res.sendRedirect(req.getContextPath() + "/login");
            }
            return;
        }

        String[] roles = rolesRequeridos(ruta);
        if (roles != null && !usuario.tieneRol(roles)) {
            if (ruta.startsWith("/api/")) {
                responderJson(res, HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para esta acción.");
            } else {
                sesion.setAttribute("flashError", "No tienes permiso para acceder a esa opción.");
                res.sendRedirect(req.getContextPath() + "/dashboard");
            }
            return;
        }

        // Las páginas con datos no deben quedar en la caché del navegador (botón "atrás" tras logout).
        if (!ruta.startsWith("/uploads/")) {
            res.setHeader("Cache-Control", "no-store");
        }
        chain.doFilter(request, response);
    }

    private boolean esPublica(String ruta) {
        return ruta.equals("/login") || ruta.startsWith("/assets/") || ruta.equals("/favicon.ico");
    }

    private String[] rolesRequeridos(String ruta) {
        String[] exacto = PERMISOS.get(ruta);
        if (exacto != null) {
            return exacto;
        }
        for (Map.Entry<String, String[]> e : PERMISOS.entrySet()) {
            if (e.getKey().endsWith("/") && ruta.startsWith(e.getKey())) {
                return e.getValue();
            }
        }
        return null;
    }

    private boolean sigueActivo(HttpSession sesion, UsuarioSesion usuario) {
        Long ultima = (Long) sesion.getAttribute(ULTIMA_VALIDACION);
        long ahora = System.currentTimeMillis();
        if (ultima != null && ahora - ultima < REVALIDAR_MS) {
            return true;
        }
        boolean activo = authService.sigueActivo(usuario.getId());
        sesion.setAttribute(ULTIMA_VALIDACION, ahora);
        return activo;
    }

    private void responderJson(HttpServletResponse res, int status, String mensaje) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"ok\":false,\"mensaje\":\"" + mensaje + "\"}");
    }
}
