package com.urbanstyle.service;

import com.urbanstyle.dao.UsuarioDAO;
import com.urbanstyle.dao.impl.UsuarioDAOImpl;
import com.urbanstyle.entity.Usuario;
import com.urbanstyle.util.PasswordUtil;
import com.urbanstyle.util.Texto;

public class AuthService {

    private final UsuarioDAO usuarioDAO;

    public AuthService() {
        this(new UsuarioDAOImpl());
    }

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /** Devuelve el usuario si las credenciales son correctas y está activo. */
    public Usuario autenticar(String username, String password) {
        String user = Texto.limpiar(username);
        if (user == null || password == null || password.isEmpty()) {
            throw new ServiceException("Ingresa tu usuario y contraseña.");
        }
        Usuario usuario = usuarioDAO.buscarPorUsername(user)
                .filter(u -> PasswordUtil.verificar(password, u.getPassword()))
                .orElseThrow(() -> new ServiceException("Usuario o contraseña incorrectos."));
        if (!usuario.isActivo()) {
            throw new ServiceException("Tu cuenta está desactivada. Contacta al administrador.");
        }
        return usuario;
    }

    /** Comprueba en cada petición que el usuario de la sesión siga activo. */
    public boolean sigueActivo(int usuarioId) {
        return usuarioDAO.buscarPorId(usuarioId).map(Usuario::isActivo).orElse(false);
    }
}
