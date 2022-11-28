package com.evervault.contracts;

import com.evervault.exceptions.HttpFailureException;
import com.evervault.models.OutboundRelayConfigResult;

import java.io.IOException;

public interface IProvideOutboundRelayConfigFromHttpApi {
    OutboundRelayConfigResult getOutboundRelayConfig(String url) throws HttpFailureException, IOException, InterruptedException;
}
