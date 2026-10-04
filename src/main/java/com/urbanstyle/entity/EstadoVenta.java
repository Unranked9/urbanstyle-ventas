package com.urbanstyle.entity;

/**
 * Estados posibles de sales.status. En la BD se guardan en inglés
 * ("completed" es el valor por defecto de la columna).
 */
public enum EstadoVenta {

    COMPLETADA("completed", "Completada"),
    ANULADA("cancelled", "Anulada");

    private final String valorBD;
    private final String etiqueta;

    EstadoVenta(String valorBD, String etiqueta) {
        this.valorBD = valorBD;
        this.etiqueta = etiqueta;
    }

    public String getValorBD() {
        return valorBD;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static EstadoVenta desdeValorBD(String valor) {
        if (valor == null) {
            return null;
        }
        for (EstadoVenta estado : values()) {
            if (estado.valorBD.equalsIgnoreCase(valor.trim())) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de venta desconocido: " + valor);
    }
}
