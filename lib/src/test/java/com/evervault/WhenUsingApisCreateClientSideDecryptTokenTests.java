package com.evervault;

import com.evervault.contracts.*;
import com.evervault.exceptions.*;
import com.evervault.services.EvervaultService;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import com.evervault.models.TokenResult;
import com.evervault.models.CreateDecryptTokenPayload;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenUsingApisCreateClientSideDecryptTokenTests {
    private final IProvideClientSideDecryptToken clientSideDecryptTokenProvider;
    private final Evervault evervaultService;
    private final IProvideCircuitBreaker circuitBreakerProvider;

    private static class Evervault extends EvervaultService {
        public void setupWrapper(IProvideClientSideDecryptToken clientSideDecryptTokenProvider,
                                 IProvideCircuitBreaker circuitBreakerProvider) {
            this.setupCircuitBreaker(circuitBreakerProvider);
            this.setupClientSideDecryptTokenProvider(clientSideDecryptTokenProvider);
        }
    }

    private static class CircuitBreakerInternal implements IProvideCircuitBreaker {
        @Override
        public <TReturn> TReturn execute(int methodIdentifier, IExecute<TReturn> executable) throws NotPossibleToHandleDataTypeException, HttpFailureException, IOException, InterruptedException {
            return executable.execute();
        }
    }

    public WhenUsingApisCreateClientSideDecryptTokenTests() {
        clientSideDecryptTokenProvider = mock(IProvideClientSideDecryptToken.class);
        circuitBreakerProvider = new CircuitBreakerInternal();
        evervaultService = new Evervault();
    }

    @Test
    void callingToCreateDecryptTokenReturnsTheHttpContent() throws HttpFailureException, IOException, InterruptedException, EvervaultException {
        var createDecryptTokenResult = new TokenResult();
        createDecryptTokenResult.token = "s0m3RunT0kenW1thNumb3rs";
        createDecryptTokenResult.expiry = 1234567890;

        when(clientSideDecryptTokenProvider.createClientSideDecryptToken(anyString(), anyString(), any())).thenReturn(createDecryptTokenResult);

        evervaultService.setupWrapper(clientSideDecryptTokenProvider, circuitBreakerProvider);

        var result = evervaultService.createClientSideDecryptToken("somecage");

        assert createDecryptTokenResult.equals(result);
    }

    @Test
    void nullParameterThrows() throws HttpFailureException, IOException, InterruptedException {
        var createDecryptTokenResult = new TokenResult();
        createDecryptTokenResult.token = "s0m3RunT0kenW1thNumb3rs";
        createDecryptTokenResult.expiry = 1234567890;
        when(clientSideDecryptTokenProvider.createClientSideDecryptToken(anyString(), anyString(), any())).thenReturn(createDecryptTokenResult);

        evervaultService.setupWrapper(clientSideDecryptTokenProvider, circuitBreakerProvider);

        assertThrows(EvervaultException.class, () -> evervaultService.createClientSideDecryptToken(null, null));
        assertThrows(EvervaultException.class, () -> evervaultService.createClientSideDecryptToken("", null));
        assertThrows(EvervaultException.class, () -> evervaultService.createClientSideDecryptToken("somecage", null));
    }

    @Test
    void providerNotSetThrows() {
        assertThrows(NullPointerException.class, () -> evervaultService.setupWrapper(null, circuitBreakerProvider));
    }
}
