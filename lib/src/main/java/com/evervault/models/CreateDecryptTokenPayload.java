package com.evervault.models;

public class CreateDecryptTokenPayload {
    String action;
    long expiry;
    Object payload;

    public CreateDecryptTokenPayload(String action, long expiry, Object payload) {
        this.action = action;
        this.expiry = expiry;
        this.payload = payload;
    }

    public CreateDecryptTokenPayload(String action, Object payload) {
        this.action = action;
        this.payload = payload;
    }
}
