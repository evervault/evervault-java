package com.evervault.http.requests;

public class CreateFunctionRunRequest {
    private Object payload;

    public CreateFunctionRunRequest(Object payload) {
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }
}
