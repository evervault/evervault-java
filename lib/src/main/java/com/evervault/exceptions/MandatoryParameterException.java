package com.evervault.exceptions;

public class MandatoryParameterException extends Exception {
    private static final String ERROR_MESSAGE = "Parameter %s is mandatory";

    public MandatoryParameterException(String parameterName) {
        super(String.format(ERROR_MESSAGE, parameterName));
    }
}
