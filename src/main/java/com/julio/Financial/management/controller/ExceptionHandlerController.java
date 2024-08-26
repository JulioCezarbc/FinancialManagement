package com.julio.Financial.management.controller;

import com.julio.Financial.management.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ErrorMessage> userNotFoundException(UserNotFoundException exception){
        ErrorMessage errorMsg = new ErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
    }
    @ExceptionHandler(EmailAlreadyInUse.class)
    private ResponseEntity<ErrorMessage> emailAlreadyInUse(EmailAlreadyInUse exception){
        ErrorMessage errorMsg = new ErrorMessage(HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMsg);
    }
    @ExceptionHandler(TransactionNotFound.class)
    private ResponseEntity<ErrorMessage> transactionNotFound(TransactionNotFound exception){
        ErrorMessage errorMsg = new ErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMsg);
    }
    @ExceptionHandler(PermissionDenied.class)
    private ResponseEntity<ErrorMessage> permissionDenied(PermissionDenied exception){
        ErrorMessage errorMsg = new ErrorMessage(HttpStatus.FORBIDDEN, exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMsg);
    }
    @ExceptionHandler(TokenCreation.class)
    private ResponseEntity<ErrorMessage> tokenCreation(TokenCreation exception){
        ErrorMessage errorMsg = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
    }
}
