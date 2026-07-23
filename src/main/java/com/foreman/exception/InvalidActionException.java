package com.foreman.exception;

public class InvalidActionException extends RuntimeException {

	private static final long serialVersionUID = 2152486081657229264L;

	public InvalidActionException(String message) {
        super(message);
    }
}
