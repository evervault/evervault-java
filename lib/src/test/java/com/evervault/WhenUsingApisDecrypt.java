package com.evervault;

import java.io.IOException;
import java.util.HashMap;

import com.evervault.contracts.IExecute;
import com.evervault.contracts.IProvideCircuitBreaker;
import com.evervault.contracts.IProvideDecrypt;
import com.evervault.exceptions.EvervaultException;
import com.evervault.exceptions.HttpFailureException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;
import com.evervault.services.EvervaultService;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenUsingApisDecrypt {
    private final IProvideDecrypt decryptProvider;
    private final Evervault evervaultService;
    private final IProvideCircuitBreaker circuitBreakerProvider;

    private static class Evervault extends EvervaultService {
        public void setupWrapper(IProvideDecrypt decryptProvider, IProvideCircuitBreaker circuitBreakerProvider) {
            this.setupCircuitBreaker(circuitBreakerProvider);
            this.setupDecryptProvider(decryptProvider);
        }
    }

    protected class CardData {
        String cardNumber;
        int cvv;
        String expiry; 
    }

    protected class SomeClass {
        String foo;
        String bar;
        String baz;
    }

    private static class CircuitBreakerInternal implements IProvideCircuitBreaker {
        @Override
        public <TReturn> TReturn execute(int methodIdentifier, IExecute<TReturn> executable) throws NotPossibleToHandleDataTypeException, HttpFailureException, IOException, InterruptedException {
            return executable.execute();
        }
    }

    public WhenUsingApisDecrypt() {
        decryptProvider = mock(IProvideDecrypt.class);
        circuitBreakerProvider = new CircuitBreakerInternal();
        evervaultService = new Evervault();
    }

    @Test
    void callingToDecryptReturnsTheHttpContent() throws HttpFailureException, IOException, InterruptedException, EvervaultException {
        var decryptResult = new CardData();
        decryptResult.cardNumber = "4242424242424242";
        decryptResult.cvv = 123;
        decryptResult.expiry = "12/24";

        var dataToDecrypt = new HashMap<String, String>();
        dataToDecrypt.put("cardNumber", "ev:abc123:$");
        dataToDecrypt.put("cvv", "ev:def456:$");
        dataToDecrypt.put("expiry", "12/24");

        when(decryptProvider.decrypt(anyString(), any(), any())).thenReturn(decryptResult);

        evervaultService.setupWrapper(decryptProvider, circuitBreakerProvider);
        
        var result = evervaultService.decrypt(dataToDecrypt, CardData.class);

        assert decryptResult.equals(result);
    }

    @Test
    void nullParameterThrows() throws HttpFailureException, IOException, InterruptedException, EvervaultException {
        var decryptResult = new CardData();
        decryptResult.cardNumber = "4242424242424242";
        decryptResult.cvv = 123;
        decryptResult.expiry = "12/24";

        var dataToDecrypt = new HashMap<String, String>();
        dataToDecrypt.put("cardNumber", "ev:abc123:$");
        dataToDecrypt.put("cvv", "ev:def456:$");
        dataToDecrypt.put("expiry", "12/24");

        when(decryptProvider.decrypt(anyString(), any(), any())).thenReturn(decryptResult);

        evervaultService.setupWrapper(decryptProvider, circuitBreakerProvider);

        assertThrows(EvervaultException.class, () -> evervaultService.decrypt(null, CardData.class));
        assertThrows(EvervaultException.class, () -> evervaultService.decrypt(dataToDecrypt, null));
    }

    @Test
    void providerNotSetThrows() {
        assertThrows(NullPointerException.class, () -> evervaultService.setupWrapper(null, circuitBreakerProvider));
    }
}
