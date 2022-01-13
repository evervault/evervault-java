package EverVault.Contracts;

import EverVault.Exceptions.HttpFailureException;

import java.io.Serializable;

public interface IProvideCageExecution {
    void runCage(String cageName, Serializable data, boolean async, String version) throws HttpFailureException;
}
