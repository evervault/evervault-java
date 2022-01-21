package EverVault.Contracts;

import EverVault.Exceptions.HttpFailureException;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;
import java.net.http.HttpTimeoutException;

public interface IExecute<TReturnType> {
     TReturnType execute() throws HttpTimeoutException, NotPossibleToHandleDataTypeException, IOException, InterruptedException, HttpFailureException;
}
