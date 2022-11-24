package com.evervault.contracts;

import com.evervault.exceptions.HttpFailureException;
import com.evervault.models.RelayOutboundConfigResult;

import java.io.IOException;

public interface IProvideRelayOutboundConfig {
    RelayOutboundConfigResult getRelayOutboundConfig(String url) throws HttpFailureException, IOException, InterruptedException;
}
