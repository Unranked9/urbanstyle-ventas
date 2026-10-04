package com.urbanstyle.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Página de inicio después del login.
 * Versión inicial: solo muestra la bienvenida. El dashboard con
 * estadísticas de ventas se agrega al final del proyecto.
 */
@WebServlet({"/dashboard", ""})
public class DashboardController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // "" es la raíz de la aplicación: se redirige al dashboard.
        if (req.getServletPath().isEmpty() || "/".equals(req.getServletPath())) {
            redirigir(req, res, "/dashboard");
            return;
        }
        vista(req, res, "dashboard", "Dashboard", "dashboard");
    }
}
