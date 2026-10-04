package com.urbanstyle.service;

/**
 * Error de negocio con un mensaje apto para mostrar al usuario
 * (por ejemplo: "Stock insuficiente para Polera Oversize Black / M").
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServiceException(String mensaje) {
        super(mensaje);
    }

    public ServiceException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
