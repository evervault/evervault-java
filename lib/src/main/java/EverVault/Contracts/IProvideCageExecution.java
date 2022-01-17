package EverVault.Contracts;

import EverVault.Exceptions.HttpFailureException;
import EverVault.ReadModels.CageRunResult;

import java.io.IOException;
import java.io.Serializable;

public interface IProvideCageExecution {
    <TResult extends Serializable> CageRunResult<TResult> runCage(String url, String cageName, Serializable data, boolean async, String version) throws HttpFailureException, IOException, InterruptedException;
}
