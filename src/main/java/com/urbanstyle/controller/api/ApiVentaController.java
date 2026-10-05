package com.urbanstyle.controller.api;

import java.io.IOException;

import com.urbanstyle.controller.BaseServlet;
import com.urbanstyle.dto.ResultadoVenta;
import com.urbanstyle.dto.UsuarioSesion;
import com.urbanstyle.dto.VentaSolicitud;
import com.urbanstyle.service.ServiceException;
import com.urbanstyle.service.VentaService;
import com.urbanstyle.util.JsonUtil;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *   POST /api/ventas/calcular    (JSON) totales sin guardar
 *   POST /api/ventas/registrar   (JSON) registra la venta
 */
@WebServlet({"/api/ventas/calcular", "/api/ventas/registrar"})
public class ApiVentaController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final VentaService ventaService = new VentaService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        VentaSolicitud solicitud = JsonUtil.leer(req, VentaSolicitud.class);
        if (solicitud == null) {
            JsonUtil.error(res, HttpServletResponse.SC_BAD_REQUEST, "Solicitud no válida.");
            return;
        }
        try {
            ResultadoVenta resultado;
            if ("/api/ventas/registrar".equals(req.getServletPath())) {
                UsuarioSesion usuario = (UsuarioSesion) req.getSession().getAttribute(BaseServlet.SESION_USUARIO);
                resultado = ventaService.registrar(solicitud, usuario.getId());
            } else {
                resultado = ventaService.calcular(solicitud);
            }
            JsonUtil.ok(res, resultado);
        } catch (ServiceException e) {
            JsonUtil.error(res, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (PersistenceException e) {
            log("Error de persistencia al procesar la venta", e);
            JsonUtil.error(res, HttpServletResponse.SC_CONFLICT,
                    "No se pudo guardar la venta (otra caja pudo registrar al mismo tiempo). Inténtalo nuevamente.");
        }
    }
}
