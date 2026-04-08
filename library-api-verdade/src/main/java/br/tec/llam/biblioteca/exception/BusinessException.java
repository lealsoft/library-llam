package br.tec.llam.biblioteca.exception;

public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
}
