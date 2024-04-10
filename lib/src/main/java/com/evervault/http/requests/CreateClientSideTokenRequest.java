package com.evervault.http.requests;

public class CreateClientSideTokenRequest {
    private String action;
    private String resource;
    private Object payload;
    private Integer expiry;

    public CreateClientSideTokenRequest(String action, String resource, Object payload, Integer expiry) {
        this.action = action;
        this.resource = resource;
        this.payload = payload;
        this.expiry = expiry;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(int expiry) {
        this.expiry = expiry;
    }
}
