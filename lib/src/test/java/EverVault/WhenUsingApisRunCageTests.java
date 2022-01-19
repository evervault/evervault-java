package EverVault;

import EverVault.Contracts.*;
import EverVault.Exceptions.HttpFailureException;
import EverVault.Exceptions.MandatoryParameterException;
import EverVault.Exceptions.MaxRetryReachedException;
import EverVault.ReadModels.CagePublicKey;
import EverVault.ReadModels.CageRunResult;
import EverVault.ReadModels.GeneratedSharedKey;
import EverVault.Services.EverVaultService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenUsingApisRunCageTests {
    private final IProvideCagePublicKeyFromHttpApi cagePublicKeyProvider;
    private final IProvideECPublicKey ecPublicKeyProvider;
    private final IProvideSharedKey sharedKeyProvider;
    private final IProvideEncryptionForObject encryptionForObjects;
    private final EverVault everVaultService;
    private final IProvideCageExecution cageExecutionProvider;

    private class EverVault extends EverVaultService {
        public void setupWrapper(IProvideCagePublicKeyFromHttpApi cagePublicKeyFromEndpointProvider,
                                 IProvideECPublicKey ecPublicKeyProvider,
                                 IProvideSharedKey sharedKeyProvider,
                                 IProvideEncryptionForObject encryptionProvider,
                                 IProvideCageExecution cageExecutionProvider) throws HttpFailureException, InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InterruptedException {
            this.setupCageExecutionProvider(cageExecutionProvider);
            this.setupKeyProviders(cagePublicKeyFromEndpointProvider, ecPublicKeyProvider, sharedKeyProvider);
            this.setupEncryption(encryptionProvider);
        }
    }

    public WhenUsingApisRunCageTests() {
        cagePublicKeyProvider = mock(IProvideCagePublicKeyFromHttpApi.class);
        ecPublicKeyProvider = mock(IProvideECPublicKey.class);
        sharedKeyProvider = mock(IProvideSharedKey.class);
        encryptionForObjects = mock(IProvideEncryptionForObject.class);
        cageExecutionProvider = mock(IProvideCageExecution.class);
        everVaultService = new EverVault();
    }

    @Test
    void callingToRunCageReturnsTheHttpContent() throws HttpFailureException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, MandatoryParameterException {
        var cagePublicKey = new CagePublicKey();
        cagePublicKey.ecdhKey = "teamEcdhKey";
        cagePublicKey.key = "key";
        cagePublicKey.teamUuid = "teamUuid";

        var generated = new GeneratedSharedKey();
        generated.SharedKey = new byte[] {};
        generated.SharedKey = new byte[] {};

        when(cagePublicKeyProvider.getCagePublicKeyFromEndpoint(any())).thenReturn(cagePublicKey);
        when(sharedKeyProvider.generateSharedKeyBasedOn(any())).thenReturn(generated);

        var cageRunResult = new CageRunResult();

        cageRunResult.result = "foo";
        cageRunResult.runId = "bar";
        when(cageExecutionProvider.runCage(anyString(), anyString(), any(), anyBoolean(), anyString())).thenReturn(cageRunResult);

        everVaultService.setupWrapper(cagePublicKeyProvider, ecPublicKeyProvider, sharedKeyProvider, encryptionForObjects, cageExecutionProvider);

        var result = everVaultService.run("somecage", "somedata", true, "1");

        assert cageRunResult.equals(result);
    }

    @Test
    void throwsMaxRetryWhenMultipleFailsOnCageRun() throws HttpFailureException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        var cagePublicKey = new CagePublicKey();
        cagePublicKey.ecdhKey = "teamEcdhKey";
        cagePublicKey.key = "key";
        cagePublicKey.teamUuid = "teamUuid";

        var generated = new GeneratedSharedKey();
        generated.SharedKey = new byte[] {};
        generated.SharedKey = new byte[] {};

        when(cagePublicKeyProvider.getCagePublicKeyFromEndpoint(any())).thenReturn(cagePublicKey);
        when(sharedKeyProvider.generateSharedKeyBasedOn(any())).thenReturn(generated);

        everVaultService.setupWrapper(cagePublicKeyProvider, ecPublicKeyProvider, sharedKeyProvider, encryptionForObjects, cageExecutionProvider);

        assertThrows(MaxRetryReachedException.class, () -> everVaultService.run("somecage", "somedata", true, "1"));
    }

    @Test
    void nullParameterThrows() throws HttpFailureException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        var cagePublicKey = new CagePublicKey();
        cagePublicKey.ecdhKey = "teamEcdhKey";
        cagePublicKey.key = "key";
        cagePublicKey.teamUuid = "teamUuid";

        var generated = new GeneratedSharedKey();
        generated.SharedKey = new byte[] {};
        generated.SharedKey = new byte[] {};

        when(cagePublicKeyProvider.getCagePublicKeyFromEndpoint(any())).thenReturn(cagePublicKey);
        when(sharedKeyProvider.generateSharedKeyBasedOn(any())).thenReturn(generated);

        var cageRunResult = new CageRunResult();

        cageRunResult.result = "foo";
        cageRunResult.runId = "bar";
        when(cageExecutionProvider.runCage(anyString(), anyString(), any(), anyBoolean(), anyString())).thenReturn(cageRunResult);

        everVaultService.setupWrapper(cagePublicKeyProvider, ecPublicKeyProvider, sharedKeyProvider, encryptionForObjects, cageExecutionProvider);

        assertThrows(MandatoryParameterException.class, () -> everVaultService.run(null, "somedata", true, "1"));
        assertThrows(MandatoryParameterException.class, () -> everVaultService.run("", "somedata", true, "1"));
        assertThrows(MandatoryParameterException.class, () -> everVaultService.run("somecage", null, true, "1"));
    }

    @Test
    void providerNotSetThrows() throws HttpFailureException, IOException, InterruptedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        var cagePublicKey = new CagePublicKey();
        cagePublicKey.ecdhKey = "teamEcdhKey";
        cagePublicKey.key = "key";
        cagePublicKey.teamUuid = "teamUuid";

        var generated = new GeneratedSharedKey();
        generated.SharedKey = new byte[] {};
        generated.SharedKey = new byte[] {};

        when(cagePublicKeyProvider.getCagePublicKeyFromEndpoint(any())).thenReturn(cagePublicKey);
        when(sharedKeyProvider.generateSharedKeyBasedOn(any())).thenReturn(generated);

        assertThrows(NullPointerException.class, () -> everVaultService.setupWrapper(cagePublicKeyProvider, ecPublicKeyProvider, sharedKeyProvider, encryptionForObjects, null));
    }
}
