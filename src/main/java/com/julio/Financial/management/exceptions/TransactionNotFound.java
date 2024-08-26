package com.julio.Financial.management.exceptions;

public class TransactionNotFound extends RuntimeException{
    public TransactionNotFound(){
        super("Transaction not found");
    }
}
