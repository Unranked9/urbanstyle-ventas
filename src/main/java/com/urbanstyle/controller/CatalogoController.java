package com.urbanstyle.controller;

import java.io.IOException;

import com.urbanstyle.service.CatalogoService;
import com.urbanstyle.service.CatalogoService.Tipo;
import com.urbanstyle.service.ServiceException;
import com.urbanstyle.util.Texto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Categorías, marcas, colores y tallas (ADMIN y SUB_ADMIN). */
@WebServlet({"/catalogos", "/catalogos/guardar", "/catalogos/eliminar"})
public class CatalogoController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final CatalogoService catalogoService = new CatalogoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Tipo tipo = Tipo.desde(req.getParameter("tipo"));
        req.setAttribute("tipo", tipo);
        req.setAttribute("tipos", Tipo.values());
        req.setAttribute("registros", catalogoService.listar(tipo));
        vista(req, res, "catalogos", "Catálogos", "catalogos");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Tipo tipo = Tipo.desde(req.getParameter("tipo"));
        Integer id = Texto.entero(req.getParameter("id"));
        try {
            if ("/catalogos/eliminar".equals(req.getServletPath())) {
                if (id == null) {
                    throw new ServiceException("Registro no válido.");
                }
                catalogoService.eliminar(tipo, id);
                ok(req, "Registro eliminado.");
            } else {
                catalogoService.guardar(tipo, id, req.getParameter("nombre"), req.getParameter("descripcion"));
                ok(req, id == null ? "Registro creado." : "Registro actualizado.");
            }
        } catch (ServiceException e) {
            error(req, e.getMessage());
        }
        redirigir(req, res, "/catalogos?tipo=" + tipo.getClave());
    }
}
