package com.team15.partpicker.exception;

public class StorageNotFoundException extends RuntimeException {

    public StorageNotFoundException(Long id) {
        super("Could not find storage " + id);
    }
}
