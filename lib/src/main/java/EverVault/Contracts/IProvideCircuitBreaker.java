package EverVault.Contracts;

import EverVault.Exceptions.HttpFailureException;
import EverVault.Exceptions.MaxRetryReachedException;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface IProvideCircuitBreaker {
    <TReturn> TReturn execute(int methodIdentifier, IExecute<TReturn> executable) throws MaxRetryReachedException, NotPossibleToHandleDataTypeException, HttpFailureException, InvalidCipherTextException, IOException, InterruptedException;
}
