package com.urbanstyle.entity;

import java.time.LocalDateTime;

import com.urbanstyle.util.Fechas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Tabla users: personal que usa el sistema (administradores y vendedores).
 */
@Entity
@Table(name = "users")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Muchos usuarios pertenecen a un rol. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Rol rol;

    @Column(name = "first_name", nullable = false, length = 60)
    private String nombres;

    @Column(name = "last_name", nullable = false, length = 60)
    private String apellidos;

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "phone", length = 15)
    private String telefono;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /** Hash BCrypt ($2a$10$...), nunca la contraseña en texto plano. */
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public Usuario() {
    }

    /** Callback de ciclo de vida: se ejecuta antes del INSERT. */
    @PrePersist
    protected void antesDeRegistrar() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    public String getNombreCompleto() {
        return ((nombres == null ? "" : nombres) + " "
                + (apellidos == null ? "" : apellidos)).trim();
    }

    public String getIniciales() {
        String a = nombres == null || nombres.isBlank() ? "" : nombres.substring(0, 1);
        String b = apellidos == null || apellidos.isBlank() ? "" : apellidos.substring(0, 1);
        return (a + b).toUpperCase();
    }

    public boolean tieneRol(String... nombresRol) {
        if (rol == null || rol.getNombre() == null) {
            return false;
        }
        for (String nombreRol : nombresRol) {
            if (rol.getNombre().equalsIgnoreCase(nombreRol)) {
                return true;
            }
        }
        return false;
    }

    public String getFechaRegistroTexto() {
        return Fechas.fecha(fechaRegistro);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
