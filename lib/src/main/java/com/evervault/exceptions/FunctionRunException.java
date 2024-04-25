package com.evervault.exceptions;

public class FunctionRunException extends Exception {

    public FunctionRunException(String message, String stack) {
        super(message + ": " + stack);
    }
}
