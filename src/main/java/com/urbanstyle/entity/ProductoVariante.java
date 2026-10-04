package com.urbanstyle.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Tabla product_variants: una combinación producto + color + talla con
 * su propio stock e imagen. La combinación es única.
 */
@Entity
@Table(name = "product_variants",
       uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "color_id", "size_id"}))
public class ProductoVariante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Producto producto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @ManyToOne(optional = false)
    @JoinColumn(name = "size_id", nullable = false)
    private Talla talla;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "image_url", length = 255)
    private String imagenUrl;

    @Column(name = "active", nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public ProductoVariante() {
    }

    @PrePersist
    protected void antesDeRegistrar() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    /** Ej.: "Black / M". */
    public String getDescripcionCorta() {
        return (color == null ? "?" : color.getNombre()) + " / "
                + (talla == null ? "?" : talla.getNombre());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Talla getTalla() {
        return talla;
    }

    public void setTalla(Talla talla) {
        this.talla = talla;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
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
