package com.evervault.TestUtils;

import java.util.Objects;

public class RelayHostResolver {
    public static String getRelayHost() {
        var envRelayHost = System.getenv("EV_RELAY_HOST");
        return Objects.requireNonNullElse(envRelayHost, "strict.relay.evervault.com");
    };
}
