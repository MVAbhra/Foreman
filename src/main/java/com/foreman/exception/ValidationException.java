package com.foreman.exception;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 5145734094795266161L;

	public ValidationException(String message) {
        super(message);
    }
}
