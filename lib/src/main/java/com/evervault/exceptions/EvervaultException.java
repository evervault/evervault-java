package com.evervault.exceptions;

// latest layer of exception before leaving Evervault domain
public class EvervaultException extends Exception {
    private final Exception nestedException;

    public EvervaultException(Exception e) {
        nestedException = e;
    }

    @Override
    public String getMessage() {
        return nestedException.getMessage();
    }

    public Exception getNestedException() {
        return nestedException;
    }
}
