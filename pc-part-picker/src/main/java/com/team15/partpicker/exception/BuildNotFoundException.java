package com.team15.partpicker.exception;

public class BuildNotFoundException extends RuntimeException {

    public BuildNotFoundException(Long id) {
        super("Could not find build " + id);
    }
}
