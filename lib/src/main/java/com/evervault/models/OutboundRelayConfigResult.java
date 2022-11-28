package com.evervault.models;

import java.util.Map;

public class OutboundRelayConfigResult {
    public Map<String, OuboundDestination> outboundDestinations;

    public OutboundRelayConfigResult(Map<String, OuboundDestination> outboundDestinations) {
        this.outboundDestinations = outboundDestinations;
    }

    public static class OuboundDestination {
        public String destinationDomain;

        public OuboundDestination(String destinationDomain) {
            this.destinationDomain = destinationDomain;
        }
    }
}
