package com.example.datn.Config;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImeiLimitExceededException extends RuntimeException {
    public ImeiLimitExceededException(String message) {
        super(message);
    }
}