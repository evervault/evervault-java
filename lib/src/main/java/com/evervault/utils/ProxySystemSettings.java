package com.evervault.utils;

import org.apache.http.HttpHost;

public class ProxySystemSettings {
    public static String PROXY_DISABLED_SCHEMES_KEY = "jdk.http.auth.tunneling.disabledSchemes";
    public static String PROXY_DISABLED_SCHEMES_VALUE = ""; //Needs to be empty as BASIC auth is disabled by default

    static String getStrictHost() {
        String DEFAULT_STRICT_HOST = "strict.relay.evervault.com";
        String strictHost = System.getenv("EV_RELAY_HOST");

        if ( strictHost == null) {
            strictHost = DEFAULT_STRICT_HOST;
        }

        return strictHost;
    };

    public static HttpHost PROXY_HOST = new HttpHost(getStrictHost(), 8443);
}
