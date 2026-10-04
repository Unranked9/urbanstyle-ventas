package com.urbanstyle.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Tabla products. El precio vive en el producto; el stock y la imagen
 * viven en cada variante (color + talla).
 */
@Entity
@Table(name = "products")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Categoria categoria;

    /** La marca es opcional (brand_id admite NULL). */
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Marca marca;

    @Column(name = "name", nullable = false, length = 100)
    private String nombre;

    @Column(name = "description", length = 255)
    private String descripcion;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio = BigDecimal.ZERO;

    @Column(name = "active", nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    /**
     * Relación bidireccional 1 a N con sus variantes. "mappedBy" indica que
     * la clave foránea (product_id) la administra ProductoVariante.
     * Las variantes no se eliminan en cascada porque pueden estar en ventas:
     * se desactivan.
     */
    @OneToMany(mappedBy = "producto", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("id ASC")
    private List<ProductoVariante> variantes = new ArrayList<>();

    public Producto() {
    }

    @PrePersist
    protected void antesDeRegistrar() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    /** Mantiene sincronizados ambos lados de la relación. */
    public void agregarVariante(ProductoVariante variante) {
        variante.setProducto(this);
        variantes.add(variante);
    }

    public int getStockTotal() {
        int total = 0;
        for (ProductoVariante v : variantes) {
            if (v.isActivo()) {
                total += v.getStock();
            }
        }
        return total;
    }

    public long getVariantesActivas() {
        return variantes.stream().filter(ProductoVariante::isActivo).count();
    }

    /** Primera imagen disponible entre sus variantes (para listados). */
    public String getImagenPrincipal() {
        for (ProductoVariante v : variantes) {
            if (v.isActivo() && v.getImagenUrl() != null && !v.getImagenUrl().isBlank()) {
                return v.getImagenUrl();
            }
        }
        for (ProductoVariante v : variantes) {
            if (v.getImagenUrl() != null && !v.getImagenUrl().isBlank()) {
                return v.getImagenUrl();
            }
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
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

    public List<ProductoVariante> getVariantes() {
        return variantes;
    }

    public void setVariantes(List<ProductoVariante> variantes) {
        this.variantes = variantes;
    }
}
