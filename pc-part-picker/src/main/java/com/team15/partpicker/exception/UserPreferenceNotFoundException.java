package com.team15.partpicker.exception;

public class UserPreferenceNotFoundException extends RuntimeException {

    public UserPreferenceNotFoundException(Long id) {
        super("Could not find user preference " + id);
    }
}
