package com.abra.revaissue.exception;

// 401
public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(String message) { super(message); }
}
