package com.julio.Financial.management.exceptions;

public class TokenCreation extends RuntimeException{
    public TokenCreation(){
        super("Error while creating token");
    }
}
