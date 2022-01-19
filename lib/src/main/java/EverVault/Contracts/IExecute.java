package EverVault.Contracts;

import EverVault.Exceptions.HttpFailureException;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.IOException;
import java.net.http.HttpTimeoutException;

public interface IExecute<TReturnType> {
     TReturnType execute() throws HttpTimeoutException, InvalidCipherTextException, NotPossibleToHandleDataTypeException, IOException, InterruptedException, HttpFailureException;
}
