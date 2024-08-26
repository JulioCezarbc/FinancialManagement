package com.julio.Financial.management.exceptions;

public class EmailAlreadyInUse extends RuntimeException{
    public EmailAlreadyInUse(){
        super("Email in use");
    }
}
