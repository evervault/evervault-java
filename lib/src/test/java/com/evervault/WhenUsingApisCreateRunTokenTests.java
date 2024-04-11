package com.evervault;

import com.evervault.contracts.*;
import com.evervault.exceptions.*;
import com.evervault.services.EvervaultService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import com.evervault.models.RunTokenResult;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenUsingApisCreateRunTokenTests {
    private final IProvideRunToken runTokenProvider;
    private final Evervault evervaultService;
    private final IProvideCircuitBreaker circuitBreakerProvider;

    private static class Evervault extends EvervaultService {
        public void setupWrapper(IProvideRunToken runTokenProvider,
                                 IProvideCircuitBreaker circuitBreakerProvider) {
            this.setupCircuitBreaker(circuitBreakerProvider);
            this.setupRunTokenProvider(runTokenProvider);
        }
    }

    private static class CircuitBreakerInternal implements IProvideCircuitBreaker {

        @Override
        public <TReturn> TReturn execute(int methodIdentifier, IExecute<TReturn> executable) throws NotPossibleToHandleDataTypeException, HttpFailureException, IOException, InterruptedException {
            return executable.execute();
        }
    }

    public WhenUsingApisCreateRunTokenTests() {
        runTokenProvider = mock(IProvideRunToken.class);
        circuitBreakerProvider = new CircuitBreakerInternal();
        evervaultService = new Evervault();
    }

    @Test
    void callingToCreateRunTokenReturnsTheHttpContent() throws HttpFailureException, IOException, InterruptedException, EvervaultException {
        RunTokenResult createRunTokenResult = new RunTokenResult();
        createRunTokenResult.token = "s0m3RunT0kenW1thNumb3rs";
        when(runTokenProvider.createRunToken(anyString(), anyString(), any())).thenReturn(createRunTokenResult);

        evervaultService.setupWrapper(runTokenProvider, circuitBreakerProvider);

        RunTokenResult result = evervaultService.createRunToken("somecage", "somedata");

        assert createRunTokenResult.equals(result);
    }

    @Test
    void nullParameterThrows() throws HttpFailureException, IOException, InterruptedException {
        RunTokenResult createRunTokenResult = new RunTokenResult();
        createRunTokenResult.token = "s0m3RunT0kenW1thNumb3rs";
        when(runTokenProvider.createRunToken(anyString(), anyString(), any())).thenReturn(createRunTokenResult);

        evervaultService.setupWrapper(runTokenProvider, circuitBreakerProvider);

        assertThrows(EvervaultException.class, () -> evervaultService.createRunToken(null, "somedata"));
        assertThrows(EvervaultException.class, () -> evervaultService.createRunToken("", "somedata"));
        assertThrows(EvervaultException.class, () -> evervaultService.createRunToken("somecage", null));
    }

    @Test
    void callingToCreateRunTokenReturnsTheHttpContentWhenNoDataPassed() throws HttpFailureException, IOException, InterruptedException, EvervaultException {
        RunTokenResult createRunTokenResult = new RunTokenResult();
        createRunTokenResult.token = "s0m3RunT0kenW1thNumb3rs";
        when(runTokenProvider.createRunToken(anyString(), anyString())).thenReturn(createRunTokenResult);

        evervaultService.setupWrapper(runTokenProvider, circuitBreakerProvider);

        RunTokenResult result = evervaultService.createRunToken("somecage");

        assert createRunTokenResult.equals(result);
    }

    @Test
    void providerNotSetThrows() {
        assertThrows(NullPointerException.class, () -> evervaultService.setupWrapper(null, circuitBreakerProvider));
    }
}
