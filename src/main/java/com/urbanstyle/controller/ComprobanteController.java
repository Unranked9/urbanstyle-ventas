package com.urbanstyle.controller;

import java.io.IOException;

import com.urbanstyle.config.AppConfig;
import com.urbanstyle.service.HistorialService;
import com.urbanstyle.service.ServiceException;
import com.urbanstyle.util.Texto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *   GET  /ventas/comprobante?id=  comprobante imprimible (boleta o factura)
 *   POST /ventas/anular           anular una venta (solo ADMIN y SUB_ADMIN, ver AuthFilter)
 */
@WebServlet({"/ventas/comprobante", "/ventas/anular"})
public class ComprobanteController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final HistorialService historialService = new HistorialService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!"/ventas/comprobante".equals(req.getServletPath())) {
            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        Integer id = Texto.entero(req.getParameter("id"));
        try {
            if (id == null) {
                throw new ServiceException("Venta no válida.");
            }
            req.setAttribute("venta", historialService.buscarDetalle(id));
            req.setAttribute("igvPorcentaje", AppConfig.igvPorcentaje());
            req.getRequestDispatcher("/WEB-INF/views/comprobante.jsp").forward(req, res);
        } catch (ServiceException e) {
            error(req, e.getMessage());
            redirigir(req, res, "/historial");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!"/ventas/anular".equals(req.getServletPath())) {
            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        Integer id = Texto.entero(req.getParameter("id"));
        try {
            if (id == null) {
                throw new ServiceException("Venta no válida.");
            }
            historialService.anular(id);
            ok(req, "Venta #" + id + " anulada. El stock fue devuelto al inventario.");
        } catch (ServiceException e) {
            error(req, e.getMessage());
        }
        redirigir(req, res, id == null ? "/historial" : "/historial/detalle?id=" + id);
    }
}
