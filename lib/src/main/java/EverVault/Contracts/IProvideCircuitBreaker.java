package EverVault.Contracts;

import EverVault.Exceptions.HttpFailureException;
import EverVault.Exceptions.MaxRetryReachedException;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;

public interface IProvideCircuitBreaker {
    <TReturn> TReturn execute(int methodIdentifier, IExecute<TReturn> executable) throws MaxRetryReachedException, NotPossibleToHandleDataTypeException, HttpFailureException, IOException, InterruptedException;
}
