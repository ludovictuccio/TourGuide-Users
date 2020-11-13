package com.tourGuide.users.web.exceptions;

public class InvalidLocationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidLocationException(String message) {
        super(message);
    }

    public InvalidLocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
