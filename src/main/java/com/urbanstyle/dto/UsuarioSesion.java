package com.urbanstyle.dto;

import java.io.Serializable;

import com.urbanstyle.entity.Rol;
import com.urbanstyle.entity.Usuario;

/**
 * Datos mínimos del usuario que se guardan en la sesión HTTP.
 * No se guarda la entidad (ni el hash de la contraseña) en la sesión.
 */
public class UsuarioSesion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final String username;
    private final String nombres;
    private final String apellidos;
    private final String rol;
    private final String rolEtiqueta;

    public UsuarioSesion(Usuario u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.nombres = u.getNombres();
        this.apellidos = u.getApellidos();
        this.rol = u.getRol().getNombre();
        this.rolEtiqueta = u.getRol().getEtiqueta();
    }

    public boolean tieneRol(String... roles) {
        for (String r : roles) {
            if (r.equalsIgnoreCase(rol)) {
                return true;
            }
        }
        return false;
    }

    /** ADMIN o SUB_ADMIN: gestionan catálogo y pueden anular ventas. */
    public boolean isGestor() {
        return tieneRol(Rol.ADMIN, Rol.SUB_ADMIN);
    }

    public boolean isAdmin() {
        return tieneRol(Rol.ADMIN);
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getNombreCompleto() {
        return (nombres + " " + apellidos).trim();
    }

    public String getIniciales() {
        String a = nombres == null || nombres.isBlank() ? "" : nombres.substring(0, 1);
        String b = apellidos == null || apellidos.isBlank() ? "" : apellidos.substring(0, 1);
        return (a + b).toUpperCase();
    }

    public String getRol() {
        return rol;
    }

    public String getRolEtiqueta() {
        return rolEtiqueta;
    }
}
