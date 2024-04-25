package com.evervault.contracts;

import com.evervault.exceptions.HttpFailureException;
import com.evervault.models.FunctionRun;

import java.io.IOException;

public interface IProvideFunctionRun {
    <T> FunctionRun<T> runFunction(String url, String functionName, Object payload, Class<T> responseType, boolean async, int timeout)
            throws HttpFailureException, IOException;
}
