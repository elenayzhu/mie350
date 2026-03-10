package com.team15.partpicker.exception;

public class CoolerNotFoundException extends RuntimeException {

    public CoolerNotFoundException(Long id) {
        super("Could not find cooler " + id);
    }
}
