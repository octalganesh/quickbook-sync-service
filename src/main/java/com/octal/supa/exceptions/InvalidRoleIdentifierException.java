package com.octal.supa.exceptions;

public class InvalidRoleIdentifierException extends RuntimeException {
    public InvalidRoleIdentifierException(String message) {
        super(message);
    }
}