package evervault.contracts;

import evervault.exceptions.HttpFailureException;
import evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;
import java.net.http.HttpTimeoutException;

public interface IExecute<TReturnType> {
     TReturnType execute() throws HttpTimeoutException, NotPossibleToHandleDataTypeException, IOException, InterruptedException, HttpFailureException;
}
