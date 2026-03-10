package com.team15.partpicker.exception;

public class RamNotFoundException extends RuntimeException {

    public RamNotFoundException(Long id) {
        super("Could not find ram " + id);
    }
}
