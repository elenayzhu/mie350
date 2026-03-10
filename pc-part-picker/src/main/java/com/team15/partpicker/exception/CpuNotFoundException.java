package com.team15.partpicker.exception;

public class CpuNotFoundException extends RuntimeException {

    public CpuNotFoundException(Long id) {
        super("Could not find cpu " + id);
    }
}
