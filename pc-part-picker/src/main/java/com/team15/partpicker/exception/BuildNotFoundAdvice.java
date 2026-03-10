package com.team15.partpicker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BuildNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(BuildNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String buildNotFoundHandler(BuildNotFoundException exception) {
        return exception.getMessage();
    }
}
