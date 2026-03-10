package com.team15.partpicker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CoolerNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(CoolerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String coolerNotFoundHandler(CoolerNotFoundException exception) {
        return exception.getMessage();
    }
}
