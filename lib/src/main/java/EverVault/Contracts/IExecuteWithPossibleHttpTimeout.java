package EverVault.Contracts;

import java.net.http.HttpTimeoutException;

public interface IExecuteWithPossibleHttpTimeout {
    <TReturnType> TReturnType execute() throws HttpTimeoutException;
}
