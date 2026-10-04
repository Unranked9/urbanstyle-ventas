package com.urbanstyle.controller;

import java.io.IOException;

import com.urbanstyle.dto.UsuarioSesion;
import com.urbanstyle.entity.Usuario;
import com.urbanstyle.service.ServiceException;
import com.urbanstyle.service.UsuarioService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet({"/perfil", "/perfil/actualizar", "/perfil/password"})
public class PerfilController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("perfil", usuarioService.buscar(usuario(req).getId()));
        vista(req, res, "perfil", "Mi perfil", "perfil");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int id = usuario(req).getId();
        try {
            if ("/perfil/password".equals(req.getServletPath())) {
                usuarioService.cambiarPassword(id, req.getParameter("actual"),
                        req.getParameter("nueva"), req.getParameter("confirmacion"));
                ok(req, "Contraseña actualizada.");
            } else {
                Usuario u = usuarioService.actualizarPerfil(id, req.getParameter("nombres"),
                        req.getParameter("apellidos"), req.getParameter("telefono"));
                // Refrescar los datos mostrados en el menú lateral.
                req.getSession().setAttribute(SESION_USUARIO, new UsuarioSesion(u));
                ok(req, "Datos actualizados.");
            }
        } catch (ServiceException e) {
            error(req, e.getMessage());
        }
        redirigir(req, res, "/perfil");
    }
}
