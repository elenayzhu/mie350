package com.team15.partpicker.exception;

public class MotherboardNotFoundException extends RuntimeException {

    public MotherboardNotFoundException(Long id) {
        super("Could not find motherboard " + id);
    }
}
