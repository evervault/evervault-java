package com.evervault.models;

public class CreateTokenPayload {
    String action;
    long expiry;
    Object payload;

    public CreateTokenPayload(String action, long expiry, Object payload) {
        this.action = action;
        this.expiry = expiry;
        this.payload = payload;
    }

    public CreateTokenPayload(String action, Object payload) {
        this.action = action;
        this.payload = payload;
    }
}
