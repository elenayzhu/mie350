package com.team15.partpicker.exception;

public class InvalidLoginException extends RuntimeException {

    public InvalidLoginException() {
        super("Invalid email or password");
    }
}
