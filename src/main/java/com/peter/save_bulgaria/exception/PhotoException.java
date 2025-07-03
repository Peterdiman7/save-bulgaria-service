package com.peter.save_bulgaria.exception;

public class PhotoException extends RuntimeException {

    public PhotoException(String message) {
        super(message);
    }

    public PhotoException(String message, Throwable cause) {
        super(message, cause);
    }
}