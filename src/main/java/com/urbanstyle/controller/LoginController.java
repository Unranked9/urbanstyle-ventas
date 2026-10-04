package com.urbanstyle.controller;

import java.io.IOException;

import com.urbanstyle.dto.UsuarioSesion;
import com.urbanstyle.entity.Usuario;
import com.urbanstyle.service.AuthService;
import com.urbanstyle.service.ServiceException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet({"/login", "/logout"})
public class LoginController extends BaseServlet {

    private static final long serialVersionUID = 1L;
    
    private static final int MAX_INTENTOS = 5;
    private static final long TIEMPO_BLOQUEO_MS = 60 * 1000L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if ("/logout".equals(req.getServletPath())) {
            HttpSession s = req.getSession(false);
            if (s != null) {
                s.invalidate();
            }
            redirigir(req, res, "/login");
            return;
        }
        if (usuario(req) != null) {
            redirigir(req, res, "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = req.getParameter("username");
        HttpSession sesion = req.getSession(true);

        Integer intentos = (Integer) sesion.getAttribute("intentosFallidos");
        Long ultimoFallo = (Long) sesion.getAttribute("ultimoFallo");

        // 1. Verificar si está bloqueado por superar los 5 intentos
        if (intentos != null && intentos >= MAX_INTENTOS && ultimoFallo != null) {
            long tiempoTranscurrido = System.currentTimeMillis() - ultimoFallo;
            if (tiempoTranscurrido < TIEMPO_BLOQUEO_MS) {
                req.setAttribute("error", "Demasiados intentos. Espera 1 minuto.");
                req.setAttribute("username", username);
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);
                return;
            } else {
                // Ya pasó el minuto de castigo: reiniciamos el contador
                sesion.removeAttribute("intentosFallidos");
                sesion.removeAttribute("ultimoFallo");
                intentos = 0;
            }
        }

        try {
            Usuario u = authService.autenticar(username, req.getParameter("password"));
            // Nueva sesión tras el login (evita fijación de sesión).
            HttpSession anterior = req.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }
            HttpSession nuevaSesion = req.getSession(true);
            nuevaSesion.setAttribute(SESION_USUARIO, new UsuarioSesion(u));
            nuevaSesion.setMaxInactiveInterval(60 * 60);
            redirigir(req, res, "/dashboard");
        } catch (ServiceException e) {
            // 2. Incrementar intentos fallidos y registrar la hora del fallo
            int conteo = (intentos == null) ? 1 : intentos + 1;
            sesion.setAttribute("intentosFallidos", conteo);
            sesion.setAttribute("ultimoFallo", System.currentTimeMillis());

            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);
        }
    }
}