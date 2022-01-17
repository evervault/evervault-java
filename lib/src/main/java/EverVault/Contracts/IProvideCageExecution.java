package EverVault.Contracts;

import EverVault.Exceptions.HttpFailureException;
import EverVault.ReadModels.CageRunResult;

import java.io.IOException;

public interface IProvideCageExecution {
    CageRunResult runCage(String url, String cageName, Object data, boolean async, String version) throws HttpFailureException, IOException, InterruptedException;
}
