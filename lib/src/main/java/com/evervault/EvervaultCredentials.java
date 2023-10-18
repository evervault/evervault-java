package com.evervault;

public class EvervaultCredentials {

    private String appId;
    private String apiKey;

    public EvervaultCredentials(
            String appId,
            String apiKey
    ) {
        if (appId == null || apiKey == null || appId.isEmpty() || appId.isEmpty()) {
            throw new IllegalArgumentException("AppId and/or ApiKey must be set.");
        }
        // check whether app id & api key match
    }

}
