package com.desafio.clientapi.exception;

public class CpfDuplicadoException extends RuntimeException {
    public CpfDuplicadoException(String message) {
        super(message);
    }
}