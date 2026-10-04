package com.urbanstyle.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convierte EstadoVenta <-> texto de la columna sales.status.
 * Con @Enumerated(STRING) se guardaría "COMPLETADA", que no coincide
 * con el valor "completed" que usa la BD.
 */
@Converter(autoApply = true)
public class EstadoVentaConverter implements AttributeConverter<EstadoVenta, String> {

    @Override
    public String convertToDatabaseColumn(EstadoVenta estado) {
        return estado == null ? null : estado.getValorBD();
    }

    @Override
    public EstadoVenta convertToEntityAttribute(String valor) {
        return EstadoVenta.desdeValorBD(valor);
    }
}
