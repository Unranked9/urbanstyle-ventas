package com.urbanstyle.service;

import java.util.List;

import com.urbanstyle.dao.UsuarioDAO;
import com.urbanstyle.dao.impl.UsuarioDAOImpl;
import com.urbanstyle.entity.Rol;
import com.urbanstyle.entity.Usuario;
import com.urbanstyle.util.PasswordUtil;
import com.urbanstyle.util.Texto;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this(new UsuarioDAOImpl());
    }

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public List<Usuario> listar() {
        return usuarioDAO.listar();
    }

    public List<Usuario> listarActivos() {
        return usuarioDAO.listarActivos();
    }

    public List<Rol> listarRoles() {
        return usuarioDAO.listarRoles();
    }

    public Usuario buscar(int id) {
        return usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new ServiceException("El usuario no existe."));
    }

    /**
     * Crea (id null) o actualiza un usuario. En la edición, una contraseña
     * vacía significa "no cambiarla".
     */
    public Usuario guardar(Integer id, Integer rolId, String nombres, String apellidos, String dni,
                           String telefono, String username, String password, int actorId) {
        nombres = Texto.limpiar(nombres);
        apellidos = Texto.limpiar(apellidos);
        dni = Texto.limpiar(dni);
        telefono = Texto.limpiar(telefono);
        username = Texto.limpiar(username);

        Validaciones.nombre(nombres, "nombres");
        Validaciones.nombre(apellidos, "apellidos");
        if (!Texto.esDni(dni)) {
            throw new ServiceException("El DNI debe tener 8 dígitos.");
        }
        if (telefono != null && !Texto.esTelefono(telefono)) {
            throw new ServiceException("El teléfono solo admite números (6 a 15 caracteres).");
        }
        if (username == null || !username.matches("[A-Za-z0-9._-]{4,50}")) {
            throw new ServiceException("El usuario debe tener entre 4 y 50 caracteres (letras, números, punto, guion).");
        }
        Rol rol = rolId == null ? null : usuarioDAO.buscarRol(rolId).orElse(null);
        if (rol == null) {
            throw new ServiceException("Selecciona un rol válido.");
        }
        if (usuarioDAO.existeDni(dni, id)) {
            throw new ServiceException("Ya existe un usuario con el DNI " + dni + ".");
        }
        if (usuarioDAO.existeUsername(username, id)) {
            throw new ServiceException("El nombre de usuario \"" + username + "\" ya está en uso.");
        }

        Usuario usuario;
        if (id == null) {
            Validaciones.password(password);
            usuario = new Usuario();
            usuario.setPassword(PasswordUtil.hashear(password));
            usuario.setActivo(true);
        } else {
            usuario = buscar(id);
            boolean dejaDeSerAdmin = usuario.tieneRol(Rol.ADMIN) && !Rol.ADMIN.equals(rol.getNombre());
            if (dejaDeSerAdmin && id == actorId) {
                throw new ServiceException("No puedes quitarte a ti mismo el rol de administrador.");
            }
            if (dejaDeSerAdmin && usuario.isActivo() && usuarioDAO.contarAdministradoresActivos() <= 1) {
                throw new ServiceException("Debe quedar al menos un administrador activo.");
            }
            if (password != null && !password.isEmpty()) {
                Validaciones.password(password);
                usuario.setPassword(PasswordUtil.hashear(password));
            }
        }
        usuario.setRol(rol);
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setDni(dni);
        usuario.setTelefono(telefono);
        usuario.setUsername(username);
        return usuarioDAO.guardar(usuario);
    }

    public void cambiarEstado(int id, boolean activo, int actorId) {
        Usuario usuario = buscar(id);
        if (!activo && id == actorId) {
            throw new ServiceException("No puedes desactivar tu propia cuenta.");
        }
        if (!activo && usuario.tieneRol(Rol.ADMIN) && usuarioDAO.contarAdministradoresActivos() <= 1) {
            throw new ServiceException("Debe quedar al menos un administrador activo.");
        }
        usuario.setActivo(activo);
        usuarioDAO.guardar(usuario);
    }

    public Usuario actualizarPerfil(int id, String nombres, String apellidos, String telefono) {
        nombres = Texto.limpiar(nombres);
        apellidos = Texto.limpiar(apellidos);
        telefono = Texto.limpiar(telefono);
        Validaciones.nombre(nombres, "nombres");
        Validaciones.nombre(apellidos, "apellidos");
        if (telefono != null && !Texto.esTelefono(telefono)) {
            throw new ServiceException("El teléfono solo admite números (6 a 15 caracteres).");
        }
        Usuario usuario = buscar(id);
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setTelefono(telefono);
        return usuarioDAO.guardar(usuario);
    }

    public void cambiarPassword(int id, String actual, String nueva, String confirmacion) {
        Usuario usuario = buscar(id);
        if (!PasswordUtil.verificar(actual, usuario.getPassword())) {
            throw new ServiceException("La contraseña actual no es correcta.");
        }
        Validaciones.password(nueva);
        if (!nueva.equals(confirmacion)) {
            throw new ServiceException("La confirmación no coincide con la nueva contraseña.");
        }
        usuario.setPassword(PasswordUtil.hashear(nueva));
        usuarioDAO.guardar(usuario);
    }
}
