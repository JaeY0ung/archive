package com.ssafy.los.backend.exception.user;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }

}
