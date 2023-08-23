package com.evervault.contracts;

import java.time.Instant;
import java.io.IOException;

import com.evervault.models.TokenResult;
import com.evervault.exceptions.HttpFailureException;

public interface IProvideClientSideToken {
    TokenResult createClientSideToken(String url, String action, Object payload, Instant expiry) throws HttpFailureException, IOException, InterruptedException;
    TokenResult createClientSideToken(String url, String action, Object payload) throws HttpFailureException, IOException, InterruptedException;
}
