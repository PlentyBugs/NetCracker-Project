package org.netcracker.project.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<Object> UserNotfound(ResponseStatusException exception){
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }
}
