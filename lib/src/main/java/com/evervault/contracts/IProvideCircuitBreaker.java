package com.evervault.contracts;

import com.evervault.exceptions.HttpFailureException;
import com.evervault.exceptions.MaxRetryReachedException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;

public interface IProvideCircuitBreaker {
    <TReturn> TReturn execute(int methodIdentifier, IExecute<TReturn> executable) throws MaxRetryReachedException, NotPossibleToHandleDataTypeException, HttpFailureException, IOException, InterruptedException;
}
