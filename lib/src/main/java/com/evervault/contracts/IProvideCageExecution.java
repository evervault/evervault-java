package com.evervault.contracts;

import com.evervault.models.CageRunResult;
import com.evervault.exceptions.HttpFailureException;

import java.io.IOException;

public interface IProvideCageExecution {
    CageRunResult runCage(String url, String cageName, Object data, boolean async, String version) throws HttpFailureException, IOException, InterruptedException;
}
