package com.evervault.contracts;

import com.evervault.exceptions.HttpFailureException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;

public interface IExecute<TReturnType> {
     TReturnType execute() throws NotPossibleToHandleDataTypeException, IOException, InterruptedException, HttpFailureException;
}
