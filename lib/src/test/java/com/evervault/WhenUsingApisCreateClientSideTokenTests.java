package com.evervault;

import com.evervault.contracts.*;
import com.evervault.exceptions.*;
import com.evervault.services.EvervaultService;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import com.evervault.models.TokenResult;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenUsingApisCreateClientSideTokenTests {
    private final IProvideClientSideToken clientSideTokenProvider;
    private final Evervault evervaultService;
    private final IProvideCircuitBreaker circuitBreakerProvider;

    private static class Evervault extends EvervaultService {
        public void setupWrapper(IProvideClientSideToken clientSideTokenProvider,
                                 IProvideCircuitBreaker circuitBreakerProvider) {
            this.setupCircuitBreaker(circuitBreakerProvider);
            this.setupClientSideTokenProvider(clientSideTokenProvider);
        }
    }

    private static class CircuitBreakerInternal implements IProvideCircuitBreaker {
        @Override
        public <TReturn> TReturn execute(int methodIdentifier, IExecute<TReturn> executable) throws NotPossibleToHandleDataTypeException, HttpFailureException, IOException, InterruptedException {
            return executable.execute();
        }
    }

    public WhenUsingApisCreateClientSideTokenTests() {
        clientSideTokenProvider = mock(IProvideClientSideToken.class);
        circuitBreakerProvider = new CircuitBreakerInternal();
        evervaultService = new Evervault();
    }

    @Test
    void callingToCreateTokenReturnsTheHttpContent() throws HttpFailureException, IOException, InterruptedException, EvervaultException {
        var createTokenResult = new TokenResult();
        createTokenResult.token = "s0m3RunT0kenW1thNumb3rs";
        createTokenResult.expiry = 1234567890;

        when(clientSideTokenProvider.createClientSideToken(anyString(), anyString(), any())).thenReturn(createTokenResult);

        evervaultService.setupWrapper(clientSideTokenProvider, circuitBreakerProvider);

        var result = evervaultService.createClientSideDecryptToken("somecage");

        assert createTokenResult.equals(result);
    }

    @Test
    void nullParameterThrows() throws HttpFailureException, IOException, InterruptedException {
        var createTokenResult = new TokenResult();
        createTokenResult.token = "s0m3RunT0kenW1thNumb3rs";
        createTokenResult.expiry = 1234567890;
        when(clientSideTokenProvider.createClientSideToken(anyString(), anyString(), any())).thenReturn(createTokenResult);

        evervaultService.setupWrapper(clientSideTokenProvider, circuitBreakerProvider);

        assertThrows(EvervaultException.class, () -> evervaultService.createClientSideDecryptToken(null, null));
        assertThrows(EvervaultException.class, () -> evervaultService.createClientSideDecryptToken("", null));
        assertThrows(EvervaultException.class, () -> evervaultService.createClientSideDecryptToken("somecage", null));
    }

    @Test
    void providerNotSetThrows() {
        assertThrows(NullPointerException.class, () -> evervaultService.setupWrapper(null, circuitBreakerProvider));
    }
}
