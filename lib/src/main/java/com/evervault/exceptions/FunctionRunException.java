package com.evervault.exceptions;

public class FunctionRunException extends Exception {
    private String runId;
    private String runStackTrace;

    public FunctionRunException(String message, String runId, String runStackTrace) {
        super(message);
        this.runId = runId;
        this.runStackTrace = runStackTrace;
    }

    public String getRunId() {
        return runId;
    }

    public String getRunStackTrace() {
        return runStackTrace;
    }

}
