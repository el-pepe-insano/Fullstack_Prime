package com.nopki.catalogo.exception;

public class JuegoNoEncontradoException extends RuntimeException {
    public JuegoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}