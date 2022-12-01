package com.evervault.models;

import java.util.HashMap;
import java.util.Map;

public class OutboundRelayConfigResult {

    public Integer pollInterval;

    public OutboundRelayConfig config;

    public OutboundRelayConfigResult(Integer pollInterval, OutboundRelayConfig config) {
        this.pollInterval = pollInterval;
        this.config = config;
    }

    public static class OutboundRelayConfig {
        public Map<String, OutboundDestination> outboundDestinations;

        public OutboundRelayConfig(HashMap<String, OutboundDestination> outboundDestinations) {
            this.outboundDestinations = outboundDestinations;
        }

        public static class OutboundDestination {
            public String destinationDomain;

            public OutboundDestination(String destinationDomain) {
                this.destinationDomain = destinationDomain;
            }
        }
    }

}
