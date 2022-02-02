package evervault.contracts;

import evervault.exceptions.HttpFailureException;
import evervault.models.CageRunResult;

import java.io.IOException;

public interface IProvideCageExecution {
    CageRunResult runCage(String url, String cageName, Object data, boolean async, String version) throws HttpFailureException, IOException, InterruptedException;
}
