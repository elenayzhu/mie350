package com.team15.partpicker.exception;

public class CaseNotFoundException extends RuntimeException {

    public CaseNotFoundException(Long id) {
        super("Could not find case " + id);
    }
}
