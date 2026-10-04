package com.urbanstyle.controller;

import java.io.IOException;

import com.urbanstyle.entity.Usuario;
import com.urbanstyle.service.ServiceException;
import com.urbanstyle.service.UsuarioService;
import com.urbanstyle.util.Texto;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Gestión de usuarios del sistema (solo ADMIN, ver AuthFilter). */
@WebServlet({"/usuarios", "/usuarios/guardar", "/usuarios/estado"})
public class UsuarioController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("usuarios", usuarioService.listar());
        req.setAttribute("roles", usuarioService.listarRoles());
        vista(req, res, "usuarios", "Usuarios", "usuarios");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int actor = usuario(req).getId();
        try {
            Integer id = Texto.entero(req.getParameter("id"));
            if ("/usuarios/estado".equals(req.getServletPath())) {
                if (id == null) {
                    throw new ServiceException("Usuario no válido.");
                }
                boolean activo = "1".equals(req.getParameter("activo"));
                usuarioService.cambiarEstado(id, activo, actor);
                ok(req, activo ? "Usuario activado." : "Usuario desactivado: ya no podrá iniciar sesión.");
            } else {
                Usuario u = usuarioService.guardar(id, Texto.entero(req.getParameter("rolId")),
                        req.getParameter("nombres"), req.getParameter("apellidos"),
                        req.getParameter("dni"), req.getParameter("telefono"),
                        req.getParameter("username"), req.getParameter("password"), actor);
                ok(req, "Usuario " + u.getUsername() + (id == null ? " creado." : " actualizado."));
            }
        } catch (ServiceException e) {
            error(req, e.getMessage());
        } catch (PersistenceException e) {
            log("Error de persistencia", e);
            error(req, "No se pudo guardar el usuario: el DNI o nombre de usuario ya existe.");
        }
        redirigir(req, res, "/usuarios");
    }
}
