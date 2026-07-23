package com.foreman.exception;

public class DuplicateResourceException extends RuntimeException {

	private static final long serialVersionUID = 2152486081657229264L;

	public DuplicateResourceException(String message) {
        super(message);
    }
}
