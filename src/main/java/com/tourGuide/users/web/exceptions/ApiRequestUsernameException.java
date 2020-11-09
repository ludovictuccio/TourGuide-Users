package com.tourGuide.users.web.exceptions;

public class ApiRequestUsernameException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApiRequestUsernameException(String message) {
        super(message);
    }

    public ApiRequestUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}
