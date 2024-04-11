package com.evervault.TestUtils;

import java.util.Objects;

public class RelayHostResolver {
    public static String getRelayHost() {
        String envRelayHost = System.getenv("EV_RELAY_HOST");
    return envRelayHost != null ? envRelayHost : "strict.relay.evervault.com";
    };
}
