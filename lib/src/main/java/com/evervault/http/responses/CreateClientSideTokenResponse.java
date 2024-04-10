package com.evervault.http.responses;

public class CreateClientSideTokenResponse {
    private String id;
    private String token;
    private Integer expiry;
    private Integer createdAt;

    public CreateClientSideTokenResponse(String id, String token, Integer expiry, Integer createdAt) {
        this.id = id;
        this.token = token;
        this.expiry = expiry;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(int expiry) {
        this.expiry = expiry;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CreateClientSideTokenResponse{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                ", expiry=" + expiry +
                ", createdAt=" + createdAt +
                '}';
    }
}
