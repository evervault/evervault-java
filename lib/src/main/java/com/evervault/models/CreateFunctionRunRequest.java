package com.evervault.models;

public class CreateFunctionRunRequest {
    Object payload;
    boolean async;

    public CreateFunctionRunRequest(Object payload, boolean async) {
        this.payload = payload;
        this.async = async;
    }

}
