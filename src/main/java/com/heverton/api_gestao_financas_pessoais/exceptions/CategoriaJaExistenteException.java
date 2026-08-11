package com.heverton.api_gestao_financas_pessoais.exceptions;

public class CategoriaJaExistenteException extends RuntimeException {
    public CategoriaJaExistenteException(String message) {
        super(message);
    }
}
