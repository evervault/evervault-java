package com.evervault;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EvervaultCredentialsTest {

    @Test
    public void constructor_CreatesInstance_ValidParameters() {
        // Given
        String validAppId = "app_28807f2a6bb1";
        String validApiKey = "ev:key:1:5L14NDMqw5AHEi1ClVvXHDvhm6Q2pyyHCnirTFGZAwcqz2P740UN4QdnGQsoKOcgB:sZ4zvj:Qqbh2V";

        // When
        EvervaultCredentials credentials = new EvervaultCredentials(validAppId, validApiKey);

        // Then
        assertNotNull(credentials);
        assertEquals(validAppId, credentials.getAppId());
        assertEquals(validApiKey, credentials.getApiKey());
    }

    @Test
    public void constructor_ThrowsIllegalArgumentException_EmptyAppId() {
        // Given
        String invalidAppId = "";
        String validApiKey = "ev:validApiKey";

        // When
        assertThrows(IllegalArgumentException.class, () -> {
            new EvervaultCredentials(invalidAppId, validApiKey);
        });
    }

    @Test
    public void constructor_ThrowsIllegalArgumentException_NullAppId() {
        // Given
        String invalidAppId = null;
        String validApiKey = "ev:validApiKeyPart1:part2:part3:validHash";

        // When
        assertThrows(IllegalArgumentException.class, () -> {
            new EvervaultCredentials(invalidAppId, validApiKey);
        });
    }

    @Test
    public void constructor_ThrowsIllegalArgumentException_EmptyApiKey() {
        // Given
        String validAppId = "app_validId";
        String invalidApiKey = "";

        // When
        assertThrows(IllegalArgumentException.class, () -> {
            new EvervaultCredentials(validAppId, invalidApiKey);
        });
    }

    @Test
    public void constructor_ThrowsIllegalArgumentException_NullApiKey() {
        // Given
        String validAppId = "app_validId";
        String invalidApiKey = null;

        // When
        assertThrows(IllegalArgumentException.class, () -> {
            new EvervaultCredentials(validAppId, invalidApiKey);
        });
    }

    @Test
    public void constructor_ThrowsIllegalArgumentException_InvalidAppIdFormat() {
        // Given
        String invalidAppId = "invalidFormatId";
        String validApiKey = "ev:validApiKey";

        // When
        assertThrows(IllegalArgumentException.class, () -> {
            new EvervaultCredentials(invalidAppId, validApiKey);
        });
    }

    @Test
    public void constructor_ThrowsIllegalArgumentException_InvalidApiKeyFormat() {
        // Given
        String validAppId = "app_validId";
        String invalidApiKey = "ev:invalidApiKey";

        // When
        assertThrows(IllegalArgumentException.class, () -> {
            new EvervaultCredentials(validAppId, invalidApiKey);
        });
    }

    @Test
    public void constructor_ThrowsIllegalArgumentException_ApiKeyDoesNotBelongToAppId() {
        // Given
        String validAppId = "app_validId";
        String invalidApiKey = "ev:key:1:5L14NDMqw5AHEi1ClVvXHDvhm6Q2pyyHCnirTFGZAwcqz2P740UN4QdnGQsoKOcgB:sZ4zvj:invalidHash";

        // When
        assertThrows(IllegalArgumentException.class, () -> {
            new EvervaultCredentials(validAppId, invalidApiKey);
        });
    }

}
