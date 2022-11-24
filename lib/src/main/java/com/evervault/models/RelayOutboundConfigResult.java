package com.evervault.models;

import java.util.Map;
import java.util.Objects;

public class RelayOutboundConfigResult {
    public Map<String, OuboundDestination> outboundDestinations;

    public static class OuboundDestination {
        public String destinationDomain;
    }
}
