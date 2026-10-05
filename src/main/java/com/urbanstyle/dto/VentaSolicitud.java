package com.urbanstyle.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos que envía la pantalla "Nueva venta" (JSON) para calcular o registrar.
 */
public class VentaSolicitud {

    private Integer clienteId;
    /** "BOLETA" o "FACTURA". */
    private String tipoComprobante;
    /** DNI (boleta) o RUC (factura) que se imprime en el comprobante. */
    private String documento;
    private List<Item> items = new ArrayList<>();

    public static class Item {
        private Integer varianteId;
        private Integer cantidad;

        public Item() {
        }

        public Item(Integer varianteId, Integer cantidad) {
            this.varianteId = varianteId;
            this.cantidad = cantidad;
        }

        public Integer getVarianteId() {
            return varianteId;
        }

        public void setVarianteId(Integer varianteId) {
            this.varianteId = varianteId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
