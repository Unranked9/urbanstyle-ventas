package com.urbanstyle.controller.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.urbanstyle.entity.Cliente;
import com.urbanstyle.service.ClienteService;
import com.urbanstyle.service.ServiceException;
import com.urbanstyle.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *   GET  /api/clientes/buscar?q=     búsqueda rápida en caja
 *   POST /api/clientes/guardar       registro rápido desde caja (form-urlencoded)
 */
@WebServlet({"/api/clientes/buscar", "/api/clientes/guardar"})
public class ApiClienteController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ClienteService clienteService = new ClienteService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Cliente c : clienteService.buscarParaVenta(req.getParameter("q"))) {
            lista.add(aMapa(c));
        }
        JsonUtil.ok(res, lista);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Cliente c = clienteService.guardar(null,
                    req.getParameter("nombres"), req.getParameter("apellidos"),
                    req.getParameter("dni"), req.getParameter("telefono"),
                    req.getParameter("email"), null, null,
                    req.getParameter("mayorista") != null);
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("ok", true);
            cuerpo.put("mensaje", "Cliente registrado.");
            cuerpo.put("cliente", aMapa(c));
            JsonUtil.ok(res, cuerpo);
        } catch (ServiceException e) {
            JsonUtil.error(res, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private Map<String, Object> aMapa(Cliente c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("nombre", c.getNombreCompleto());
        m.put("dni", c.getDni());
        m.put("email", c.getEmail());
        m.put("telefono", c.getTelefono());
        m.put("mayorista", c.isMayorista());
        return m;
    }
}
