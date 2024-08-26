package com.julio.Financial.management.exceptions;

public class PermissionDenied extends RuntimeException{
    public PermissionDenied(){
        super("You do not have permission for this request");
    }
}
