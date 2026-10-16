package com.urbanstyle.controller;

import java.io.IOException;

import com.urbanstyle.service.DashboardService;
import com.urbanstyle.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet({"/dashboard", ""})
public class DashboardController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final DashboardService dashboardService = new DashboardService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getServletPath().isEmpty() || "/".equals(req.getServletPath())) {
            redirigir(req, res, "/dashboard");
            return;
        }
        var resumen = dashboardService.resumen();
        req.setAttribute("resumen", resumen);
        req.setAttribute("etiquetasJson", JsonUtil.aJson(resumen.getEtiquetasSemana()));
        req.setAttribute("totalesJson", JsonUtil.aJson(resumen.getTotalesSemana()));
        vista(req, res, "dashboard", "Dashboard", "dashboard");
    }
}
