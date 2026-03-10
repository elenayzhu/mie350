package com.team15.partpicker.exception;

public class GpuNotFoundException extends RuntimeException {

    public GpuNotFoundException(Long id) {
        super("Could not find gpu " + id);
    }
}
