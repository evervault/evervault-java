package com.evervault.models;

import java.util.Map;

public class OutboundRelayConfig {
  public String appUuid;

  public String teamUuid;

  public boolean strictMode;

  public Map<String, OutboundRelayConfigMetadata> outboundDestinations;
}