package com.urbanstyle.entity;

/**
 * payment_documents.document_type. Se guarda con @Enumerated(STRING),
 * es decir como el texto "BOLETA" o "FACTURA".
 */
public enum TipoComprobante {

    BOLETA("Boleta de venta"),
    FACTURA("Factura");

    private final String etiqueta;

    TipoComprobante(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
