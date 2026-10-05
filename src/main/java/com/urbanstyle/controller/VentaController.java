package com.urbanstyle.controller;

import java.io.IOException;

import com.urbanstyle.config.AppConfig;
import com.urbanstyle.service.VentaService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GET /ventas/nueva : pantalla de caja.
 * El cálculo y el registro se hacen por AJAX en /api/ventas/* (ApiVentaController).
 */
@WebServlet("/ventas/nueva")
public class VentaController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("igvPorcentaje", AppConfig.igvPorcentaje());
        req.setAttribute("montoBoletaDni", VentaService.MONTO_BOLETA_CON_DNI);
        vista(req, res, "nueva-venta", "Nueva venta", "nuevaVenta");
    }
}
