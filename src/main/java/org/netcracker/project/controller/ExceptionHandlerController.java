package org.netcracker.project.controller;

import org.netcracker.project.exception.UserNotfoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(value = UserNotfoundException.class)
    public ResponseEntity<Object> UserNotfound(UserNotfoundException exception){
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }
}
