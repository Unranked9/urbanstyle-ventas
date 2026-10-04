package com.urbanstyle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Tabla roles: ADMIN, SUB_ADMIN, SELLER.
 */
@Entity
@Table(name = "roles")
public class Rol {

    public static final String ADMIN = "ADMIN";
    public static final String SUB_ADMIN = "SUB_ADMIN";
    public static final String SELLER = "SELLER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String nombre;

    public Rol() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Nombre amigable para mostrar en pantalla. */
    public String getEtiqueta() {
        if (nombre == null) {
            return "";
        }
        return switch (nombre) {
            case ADMIN -> "Administrador";
            case SUB_ADMIN -> "Sub administrador";
            case SELLER -> "Vendedor";
            default -> nombre;
        };
    }
}
