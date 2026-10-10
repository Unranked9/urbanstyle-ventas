package com.urbanstyle.dto;

import java.time.LocalDate;

import com.urbanstyle.entity.EstadoVenta;
import com.urbanstyle.entity.TipoComprobante;

/**
 * Filtros del historial de ventas. Los valores nulos no filtran.
 */
public class FiltroVentas {

    private LocalDate desde;
    private LocalDate hasta;
    private EstadoVenta estado;
    private TipoComprobante tipoComprobante;
    private Integer usuarioId;
    private String texto;
    private int limite = 300;

    public LocalDate getDesde() {
        return desde;
    }

    public void setDesde(LocalDate desde) {
        this.desde = desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public void setHasta(LocalDate hasta) {
        this.hasta = hasta;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    public TipoComprobante getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(TipoComprobante tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }
}
