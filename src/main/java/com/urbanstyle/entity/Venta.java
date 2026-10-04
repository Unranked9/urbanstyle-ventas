package com.urbanstyle.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.urbanstyle.util.Fechas;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Tabla sales (cabecera de la venta).
 */
@Entity
@Table(name = "sales")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Vendedor que registró la venta. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;

    /** Cliente opcional: una venta puede ser a un cliente no registrado. */
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "sale_date", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "igv", nullable = false, precision = 10, scale = 2)
    private BigDecimal igv = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Convert(converter = EstadoVentaConverter.class)
    @Column(name = "status", nullable = false, length = 20)
    private EstadoVenta estado = EstadoVenta.COMPLETADA;

    /** Al guardar la venta se guardan también sus detalles (cascade PERSIST). */
    @OneToMany(mappedBy = "venta", cascade = CascadeType.PERSIST)
    @OrderBy("id ASC")
    private List<DetalleVenta> detalles = new ArrayList<>();

    /** Boleta o factura emitida para esta venta. */
    @OneToOne(mappedBy = "venta", cascade = CascadeType.PERSIST)
    private ComprobantePago comprobante;

    public Venta() {
    }

    @PrePersist
    protected void antesDeRegistrar() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public void agregarDetalle(DetalleVenta detalle) {
        detalle.setVenta(this);
        detalles.add(detalle);
    }

    public void asignarComprobante(ComprobantePago comprobante) {
        comprobante.setVenta(this);
        this.comprobante = comprobante;
    }

    public int getTotalPrendas() {
        int total = 0;
        for (DetalleVenta d : detalles) {
            total += d.getCantidad();
        }
        return total;
    }

    public boolean isAnulada() {
        return estado == EstadoVenta.ANULADA;
    }

    /** Fecha formateada para las vistas JSP. */
    public String getFechaTexto() {
        return Fechas.fechaHora(fecha);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    public ComprobantePago getComprobante() {
        return comprobante;
    }

    public void setComprobante(ComprobantePago comprobante) {
        this.comprobante = comprobante;
    }
}
