package com.evervault.models;

public class FunctionRun<T> {
    private String id;
    private String status;
    private T result;
    private FunctionRunError error;

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public T getResult() {
        return result;
    }

    public FunctionRunError getError() {
        return error;
    }

    public class FunctionRunError {
        private String message;
        private String stack;

        public String getMessage() {
            return message;
        }

        public String getStack() {
            return stack;
        }
    }
}
