package evervault.contracts;

import evervault.exceptions.HttpFailureException;
import evervault.exceptions.MaxRetryReachedException;
import evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;

public interface IProvideCircuitBreaker {
    <TReturn> TReturn execute(int methodIdentifier, IExecute<TReturn> executable) throws MaxRetryReachedException, NotPossibleToHandleDataTypeException, HttpFailureException, IOException, InterruptedException;
}
