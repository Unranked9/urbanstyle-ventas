package com.urbanstyle.entity;

import java.time.LocalDateTime;

import com.urbanstyle.util.Fechas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Tabla clientes.
 *
 * La BD exige email, username y password para todo cliente (pensado para
 * una futura tienda en línea). Cuando el cliente se registra desde caja,
 * ClienteService genera el username y una contraseña aleatoria.
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 60)
    private String nombres;

    @Column(name = "last_name", nullable = false, length = 60)
    private String apellidos;

    @Column(name = "dni", unique = true, length = 8)
    private String dni;

    @Column(name = "phone", length = 15)
    private String telefono;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    /** is_wholesaler: cliente mayorista. */
    @Column(name = "is_wholesaler", nullable = false)
    private boolean mayorista;

    @Column(name = "active", nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public Cliente() {
    }

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

    public String getFechaRegistroTexto() {
        return Fechas.fecha(fechaRegistro);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isMayorista() {
        return mayorista;
    }

    public void setMayorista(boolean mayorista) {
        this.mayorista = mayorista;
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
