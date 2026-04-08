package br.tec.llam.biblioteca.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s não encontrado com ID: %s", resource, id));
    }
}
