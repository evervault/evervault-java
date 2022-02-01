package evervault.Contracts;

import evervault.Exceptions.HttpFailureException;
import evervault.Exceptions.MaxRetryReachedException;
import evervault.Exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;

public interface IProvideCircuitBreaker {
    <TReturn> TReturn execute(int methodIdentifier, IExecute<TReturn> executable) throws MaxRetryReachedException, NotPossibleToHandleDataTypeException, HttpFailureException, IOException, InterruptedException;
}
