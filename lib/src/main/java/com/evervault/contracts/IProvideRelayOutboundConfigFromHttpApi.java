package com.evervault.contracts;

import com.evervault.exceptions.HttpFailureException;
import com.evervault.models.OutboundRelayConfig;

import java.io.IOException;

public interface IProvideRelayOutboundConfigFromHttpApi {
    OutboundRelayConfig getRelayOutboundConfig(String url) throws IOException, InterruptedException, HttpFailureException;
}