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
        try {
            Usuario u = authService.autenticar(username, req.getParameter("password"));
            // Nueva sesión tras el login (evita fijación de sesión).
            HttpSession anterior = req.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }
            HttpSession sesion = req.getSession(true);
            sesion.setAttribute(SESION_USUARIO, new UsuarioSesion(u));
            sesion.setMaxInactiveInterval(60 * 60);
            redirigir(req, res, "/dashboard");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);
        }
    }
}
