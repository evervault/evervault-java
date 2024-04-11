package com.evervault;

import com.evervault.contracts.*;
import com.evervault.exceptions.*;
import com.evervault.models.CagePublicKey;
import com.evervault.models.CageRunResult;
import com.evervault.models.GeneratedSharedKey;
import com.evervault.services.EvervaultService;
import com.evervault.utils.EcdhCurve;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenUsingApisRunCageTests {
    private final IProvideCagePublicKeyFromHttpApi cagePublicKeyProvider;
    private final IProvideECPublicKey ecPublicKeyProvider;
    private final IProvideSharedKey sharedKeyProvider;
    private final IProvideEncryptionForObject encryptionForObjects;
    private final Evervault evervaultService;
    private final IProvideCageExecution cageExecutionProvider;
    private final IProvideCircuitBreaker circuitBreakerProvider;
    private final IProvideTime timeProvider;

    private class Evervault extends EvervaultService {
        public void setupWrapper(IProvideCagePublicKeyFromHttpApi cagePublicKeyFromEndpointProvider,
                                 IProvideECPublicKey ecPublicKeyProvider,
                                 IProvideSharedKey sharedKeyProvider,
                                 IProvideEncryptionForObject encryptionProvider,
                                 IProvideCageExecution cageExecutionProvider,
                                 IProvideCircuitBreaker circuitBreakerProvider,
                                 IProvideTime timeProvider,
                                 EcdhCurve ecdhCurve) throws EvervaultException {
            this.setupCircuitBreaker(circuitBreakerProvider);
            this.setupCageExecutionProvider(cageExecutionProvider);
            this.setupKeyProviders(cagePublicKeyFromEndpointProvider, ecPublicKeyProvider, sharedKeyProvider, timeProvider, ecdhCurve);
            this.setupEncryption(encryptionProvider);
        }
    }

    private class CircuitBreakerInternal implements IProvideCircuitBreaker {

        @Override
        public <TReturn> TReturn execute(int methodIdentifier, IExecute<TReturn> executable) throws NotPossibleToHandleDataTypeException, HttpFailureException, IOException, InterruptedException {
            return executable.execute();
        }
    }

    public WhenUsingApisRunCageTests() {
        cagePublicKeyProvider = mock(IProvideCagePublicKeyFromHttpApi.class);
        ecPublicKeyProvider = mock(IProvideECPublicKey.class);
        sharedKeyProvider = mock(IProvideSharedKey.class);
        encryptionForObjects = mock(IProvideEncryptionForObject.class);
        cageExecutionProvider = mock(IProvideCageExecution.class);
        circuitBreakerProvider = new CircuitBreakerInternal();
        timeProvider = mock(IProvideTime.class);
        evervaultService = new Evervault();
    }

    @Test
    void callingToRunCageReturnsTheHttpContent() throws Asn1EncodingException, HttpFailureException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NotPossibleToHandleDataTypeException, InvalidCipherTextException, MaxRetryReachedException, NoSuchProviderException, NotImplementedException, EvervaultException {
        CagePublicKey cagePublicKey = new CagePublicKey();
        cagePublicKey.ecdhKey = "teamEcdhKey";
        cagePublicKey.key = "key";
        cagePublicKey.teamUuid = "teamUuid";

        GeneratedSharedKey generated = new GeneratedSharedKey();
        generated.SharedKey = new byte[]{};
        generated.SharedKey = new byte[]{};

        when(cagePublicKeyProvider.getCagePublicKeyFromEndpoint(any())).thenReturn(cagePublicKey);
        when(sharedKeyProvider.generateSharedKeyBasedOn(any())).thenReturn(generated);

        CageRunResult cageRunResult = new CageRunResult();

        cageRunResult.result = "foo";
        cageRunResult.runId = "bar";
        when(cageExecutionProvider.runCage(anyString(), anyString(), any(), anyBoolean(), anyString())).thenReturn(cageRunResult);

        evervaultService.setupWrapper(cagePublicKeyProvider, ecPublicKeyProvider, sharedKeyProvider, encryptionForObjects, cageExecutionProvider, circuitBreakerProvider, timeProvider, EcdhCurve.SECP256K1);

        CageRunResult result = evervaultService.run("somecage", "somedata", true, "1");

        assert cageRunResult.equals(result);
    }

    @Test
    void nullParameterThrows() throws Asn1EncodingException, HttpFailureException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotImplementedException, EvervaultException {
        CagePublicKey cagePublicKey = new CagePublicKey();
        cagePublicKey.ecdhKey = "teamEcdhKey";
        cagePublicKey.key = "key";
        cagePublicKey.teamUuid = "teamUuid";

        GeneratedSharedKey generated = new GeneratedSharedKey();
        generated.SharedKey = new byte[]{};
        generated.SharedKey = new byte[]{};

        when(cagePublicKeyProvider.getCagePublicKeyFromEndpoint(any())).thenReturn(cagePublicKey);
        when(sharedKeyProvider.generateSharedKeyBasedOn(any())).thenReturn(generated);

        CageRunResult cageRunResult = new CageRunResult();

        cageRunResult.result = "foo";
        cageRunResult.runId = "bar";
        when(cageExecutionProvider.runCage(anyString(), anyString(), any(), anyBoolean(), anyString())).thenReturn(cageRunResult);

        evervaultService.setupWrapper(cagePublicKeyProvider, ecPublicKeyProvider, sharedKeyProvider, encryptionForObjects, cageExecutionProvider, circuitBreakerProvider, timeProvider, EcdhCurve.SECP256K1);

        assertThrows(EvervaultException.class, () -> evervaultService.run(null, "somedata", true, "1"));
        assertThrows(EvervaultException.class, () -> evervaultService.run("", "somedata", true, "1"));
        assertThrows(EvervaultException.class, () -> evervaultService.run("somecage", null, true, "1"));
    }

    @Test
    void providerNotSetThrows() throws Asn1EncodingException, HttpFailureException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotImplementedException {
        CagePublicKey cagePublicKey = new CagePublicKey();
        cagePublicKey.ecdhKey = "teamEcdhKey";
        cagePublicKey.key = "key";
        cagePublicKey.teamUuid = "teamUuid";

        GeneratedSharedKey generated = new GeneratedSharedKey();
        generated.SharedKey = new byte[]{};
        generated.SharedKey = new byte[]{};

        when(cagePublicKeyProvider.getCagePublicKeyFromEndpoint(any())).thenReturn(cagePublicKey);
        when(sharedKeyProvider.generateSharedKeyBasedOn(any())).thenReturn(generated);
        when(timeProvider.GetNow()).thenReturn(Instant.now());

        assertThrows(NullPointerException.class, () -> evervaultService.setupWrapper(cagePublicKeyProvider, ecPublicKeyProvider, sharedKeyProvider, encryptionForObjects, null, circuitBreakerProvider, timeProvider, EcdhCurve.SECP256K1));
    }
}
