package com.urbanstyle.controller;

import java.io.IOException;

import com.urbanstyle.entity.Cliente;
import com.urbanstyle.service.ClienteService;
import com.urbanstyle.service.ServiceException;
import com.urbanstyle.util.Texto;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet({"/clientes", "/clientes/guardar", "/clientes/estado"})
public class ClienteController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ClienteService clienteService = new ClienteService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String texto = Texto.limpiar(req.getParameter("q"));
        String estado = req.getParameter("estado");
        Boolean activo = "inactivos".equals(estado) ? Boolean.FALSE
                : "todos".equals(estado) ? null : Boolean.TRUE;
        String tipo = req.getParameter("tipo");
        Boolean mayorista = "mayoristas".equals(tipo) ? Boolean.TRUE
                : "minoristas".equals(tipo) ? Boolean.FALSE : null;
        req.setAttribute("clientes", clienteService.listar(texto, activo, mayorista));
        req.setAttribute("fq", texto);
        req.setAttribute("fEstado", estado == null ? "activos" : estado);
        req.setAttribute("fTipo", tipo == null ? "todos" : tipo);
        vista(req, res, "clientes", "Clientes", "clientes");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            if ("/clientes/estado".equals(req.getServletPath())) {
                Integer id = Texto.entero(req.getParameter("id"));
                boolean activo = "1".equals(req.getParameter("activo"));
                if (id == null) {
                    throw new ServiceException("Cliente no válido.");
                }
                clienteService.cambiarEstado(id, activo);
                ok(req, activo ? "Cliente activado." : "Cliente desactivado.");
            } else {
                Integer id = Texto.entero(req.getParameter("id"));
                Cliente c = clienteService.guardar(id,
                        req.getParameter("nombres"), req.getParameter("apellidos"),
                        req.getParameter("dni"), req.getParameter("telefono"),
                        req.getParameter("email"), req.getParameter("username"),
                        req.getParameter("password"), req.getParameter("mayorista") != null);
                ok(req, "Cliente " + c.getNombreCompleto() + (id == null ? " registrado." : " actualizado."));
            }
        } catch (ServiceException e) {
            error(req, e.getMessage());
        } catch (PersistenceException e) {
            log("Error de persistencia", e);
            error(req, "No se pudo guardar el cliente: el DNI, correo o usuario ya existe.");
        }
        redirigir(req, res, "/clientes");
    }
}
