package com.abra.revaissue.exception;

public class UnauthorizedOperation extends RuntimeException{
    public UnauthorizedOperation(String message){
        super(message);
    }
}
