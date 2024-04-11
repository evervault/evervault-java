package com.evervault.EndToEndTests;

import com.evervault.exceptions.EvervaultException;
import com.evervault.models.TokenResult;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

public class ClientSideTokenTest extends EndToEndTest {

    @Test
    void itCreatesAClientSideToken() throws EvervaultException {
        String payload = (String) evervault.encrypt("hello world!");
        TokenResult result = evervault.createClientSideDecryptToken(payload);
        assert(result.token != null);
        assert(result.expiry > 0);
    }

    @Test
    void itCreatesAClientSideTokenWithExpiry() throws EvervaultException {
        String payload = (String) evervault.encrypt("hello world!");
        TokenResult result = evervault.createClientSideDecryptToken(payload, Instant.now().plus(Duration.ofMinutes(2)));
        assert(result.token != null);
        assert(result.expiry > 0);
        System.out.println(result.token);
    }

}