package com.evervault.http.responses;

import com.google.gson.Gson;

import java.util.Map;

public class CreateFunctionRunResponse {
    private String id;
    private String status;
    private Map<String, Object> result;
    private FunctionRunError error;
    private Integer createdAt;

    public CreateFunctionRunResponse(String id, String status, Map<String, Object> result, FunctionRunError error, Integer createdAt) {
        this.id = id;
        this.status = status;
        this.result = result;
        this.error = error;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public FunctionRunError getError() {
        return error;
    }

    public void setError(FunctionRunError error) {
        this.error = error;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public class FunctionRunError {
        private String message;
        private String stack;

        public FunctionRunError(String message, String stack) {
            this.message = message;
            this.stack = stack;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStack() {
            return stack;
        }

        public void setStack(String stack) {
            this.stack = stack;
        }
    }
}
