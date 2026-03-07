package com.team15.partpicker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GpuNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(GpuNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String gpuNotFoundHandler(GpuNotFoundException exception) {
        return exception.getMessage();
    }
}
