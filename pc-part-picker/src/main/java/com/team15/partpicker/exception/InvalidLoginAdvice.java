package com.team15.partpicker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InvalidLoginAdvice {

    @ResponseBody
    @ExceptionHandler(InvalidLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String invalidLoginHandler(InvalidLoginException exception) {
        return exception.getMessage();
    }
}
