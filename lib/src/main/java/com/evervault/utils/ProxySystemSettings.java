package com.evervault.utils;

public class ProxySystemSettings {
    public static String PROXY_DISABLED_SCHEMES_KEY = "jdk.http.auth.tunneling.disabledSchemes";
    public static String PROXY_DISABLED_SCHEMES_VALUE = ""; //Needs to be empty as BASIC auth is disabled by default
}
