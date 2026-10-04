package com.urbanstyle.entity;

import java.time.LocalDateTime;

import com.urbanstyle.util.Fechas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Tabla payment_documents: boleta o factura de una venta.
 * (tipo, serie, número) es único: B001-00000001, F001-00000001...
 */
@Entity
@Table(name = "payment_documents",
       uniqueConstraints = @UniqueConstraint(columnNames = {"document_type", "series", "number"}))
public class ComprobantePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(optional = false)
    @JoinColumn(name = "sale_id", nullable = false)
    private Venta venta;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 10)
    private TipoComprobante tipo;

    @Column(name = "series", nullable = false, length = 10)
    private String serie;

    @Column(name = "number", nullable = false)
    private int numero;

    @Column(name = "customer_dni", length = 8)
    private String dniCliente;

    @Column(name = "customer_ruc", length = 11)
    private String rucCliente;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime fechaEmision;

    public ComprobantePago() {
    }

    @PrePersist
    protected void antesDeRegistrar() {
        if (fechaEmision == null) {
            fechaEmision = LocalDateTime.now();
        }
    }

    /** Ej.: B001-00000025 */
    public String getNumeroCompleto() {
        return serie + "-" + String.format("%08d", numero);
    }

    public String getFechaEmisionTexto() {
        return Fechas.fechaHora(fechaEmision);
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

    public TipoComprobante getTipo() {
        return tipo;
    }

    public void setTipo(TipoComprobante tipo) {
        this.tipo = tipo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public String getRucCliente() {
        return rucCliente;
    }

    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
}
