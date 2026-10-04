package com.urbanstyle.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Tabla sale_details: una línea de la venta.
 * unit_price guarda el precio vigente al momento de vender, de modo que
 * cambiar el precio del producto no altera ventas pasadas.
 */
@Entity
@Table(name = "sale_details")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sale_id", nullable = false)
    private Venta venta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductoVariante variante;

    @Column(name = "quantity", nullable = false)
    private int cantidad;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalLinea = BigDecimal.ZERO;

    public DetalleVenta() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public ProductoVariante getVariante() {
        return variante;
    }

    public void setVariante(ProductoVariante variante) {
        this.variante = variante;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(BigDecimal totalLinea) {
        this.totalLinea = totalLinea;
    }
}
