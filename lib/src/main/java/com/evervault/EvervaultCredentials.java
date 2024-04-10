package com.evervault;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Represents credentials for authentication with the Evervault platform, ensuring that both
 * the application ID and API key are valid through internal checks upon
 * object creation.
 */
public class EvervaultCredentials {

    private final String appId;
    private final String apiKey;

    /**
     * Constructs a new set of credentials for authenticating with Evervault.
     * This constructor validates the provided app ID and API key,
     * throwing an IllegalArgumentException if either of them is invalid.
     *
     * @param appId  the unique identifier for the app, retrieved
     *               through the Evervault dashboard. It should start with "app_".
     * @param apiKey the API key associated with the app, also
     *               retrieved through the Evervault dashboard. It should be in
     *               a specific format, starting with "ev:".
     * @throws IllegalArgumentException if the app ID or API key is
     *                                  null, empty, or doesn't conform to expected formats.
     */
    public EvervaultCredentials(
            String appId,
            String apiKey
    ) {
        validateAppIdAndApiKey(appId, apiKey);
        this.appId = appId;
        this.apiKey = apiKey;
    }

    /**
     * Retrieves the app ID associated with these credentials.
     *
     * @return the app ID.
     */
    public String getAppId() {
        return appId;
    }

    /**
     * Retrieves the API key associated with these credentials.
     *
     * @return the API key.
     */
    public String getApiKey() {
        return apiKey;
    }

    private void validateAppIdAndApiKey(String appId, String apiKey) {
        if (appId == null || appId.isEmpty()) {
            throw new IllegalArgumentException("No App ID provided. The App ID can be retrieved in the Evervault dashboard (App Settings).");
        }

        if (!appId.startsWith("app_")) {
            throw new IllegalArgumentException("The provided App ID is invalid. The App ID can be retrieved in the Evervault dashboard (App Settings).");
        }

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("No API key provided. API Keys can be created or retrieved in the Evervault dashboard (App Settings > API Keys).");
        }

        if (apiKey.startsWith("ev:")) {
            String appUuidHash;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                byte[] hash = md.digest(appId.getBytes(StandardCharsets.UTF_8));
                appUuidHash = Base64.getEncoder().encodeToString(hash).substring(0, 6);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Error computing hash: ", e);
            }
            String[] parts = apiKey.split(":");
            if (parts.length < 5) {
                throw new IllegalArgumentException("The provided API key is not in a recognized format. API Keys can be created or retrieved in the Evervault dashboard (App Settings > API Keys).");
            }
            String appUuidHashFromApiKey = parts[4];
            if (!appUuidHash.equals(appUuidHashFromApiKey)) {
                throw new IllegalArgumentException("The provided API key does not belong to the App " + appId + ".");
            }
        }
    }
}
