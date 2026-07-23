package com.foreman.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 5145734094795266161L;

	public ResourceNotFoundException(String message) {
        super(message);
    }
}
