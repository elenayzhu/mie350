package com.team15.partpicker.exception;

public class PsuNotFoundException extends RuntimeException {

    public PsuNotFoundException(Long id) {
        super("Could not find psu " + id);
    }
}
