package com.tourGuide.users.web.exceptions;

public class UserInputException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserInputException(String message) {
        super(message);
    }

    public UserInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
