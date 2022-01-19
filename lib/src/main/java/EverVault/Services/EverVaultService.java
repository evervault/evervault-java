/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package EverVault.Services;

import EverVault.Contracts.*;
import EverVault.Exceptions.HttpFailureException;
import EverVault.Exceptions.MandatoryParameterException;
import EverVault.Exceptions.MaxRetryReachedException;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import EverVault.ReadModels.CageRunResult;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public abstract class EverVaultService {
    protected IProvideCagePublicKeyFromHttpApi cagePublicKeyFromEndpointProvider;
    protected IProvideECPublicKey ecPublicKeyProvider;
    protected IProvideSharedKey sharedKeyProvider;
    protected IProvideEncryptionForObject encryptionProvider;
    protected IProvideCageExecution cageExecutionProvider;
    protected IProvideCircuitBreaker circuitBreakerProvider;

    protected final int getCageHash = "getCagePublicKeyFromEndpoint".hashCode();
    protected final int runCageHash = "runCage".hashCode();

    protected byte[] generatedEcdhKey;
    protected byte[] sharedKey;

    // Virtual method
    protected String getEverVaultBaseUrl() {
        return "";
    }

    // Virtual method
    protected String getEverVaultRunUrl() {
        return "";
    }

    protected void setupCircuitBreaker(IProvideCircuitBreaker provideCircuitBreaker) {
//        if (provideCircuitBreaker == null) {
//            throw new NullPointerException(IProvideCircuitBreaker.class.getName());
//        }

        this.circuitBreakerProvider = provideCircuitBreaker;
    }

    protected void setupCageExecutionProvider(IProvideCageExecution cageExecutionProvider) {
        if (cageExecutionProvider == null) {
            throw new NullPointerException(IProvideCageExecution.class.getName());
        }

        this.cageExecutionProvider = cageExecutionProvider;
    }

    protected void setupKeyProviders(IProvideCagePublicKeyFromHttpApi cagePublicKeyFromEndpointProvider,
                                     IProvideECPublicKey ecPublicKeyProvider,
                                     IProvideSharedKey sharedKeyProvider) throws HttpFailureException, InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InterruptedException, NotPossibleToHandleDataTypeException, InvalidCipherTextException, MaxRetryReachedException {
        if (cagePublicKeyFromEndpointProvider == null) {
            throw new NullPointerException(IProvideCagePublicKeyFromHttpApi.class.getName());
        }

        if (ecPublicKeyProvider == null) {
            throw new NullPointerException(IProvideECPublicKey.class.getName());
        }

        if (sharedKeyProvider == null) {
            throw new NullPointerException(IProvideSharedKey.class.getName());
        }

        this.cagePublicKeyFromEndpointProvider = cagePublicKeyFromEndpointProvider;
        this.ecPublicKeyProvider = ecPublicKeyProvider;
        this.sharedKeyProvider = sharedKeyProvider;

        setupKeys();
    }

    protected void setupEncryption(IProvideEncryptionForObject encryptionProvider) {
        if (encryptionProvider == null) {
            throw new NullPointerException(IProvideSharedKey.class.getName());
        }

        this.encryptionProvider = encryptionProvider;
    }

    private void setupKeys() throws HttpFailureException, IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, NotPossibleToHandleDataTypeException, InvalidCipherTextException, MaxRetryReachedException {
        var teamEcdhKey = circuitBreakerProvider.execute(getCageHash, () -> cagePublicKeyFromEndpointProvider.getCagePublicKeyFromEndpoint(getEverVaultBaseUrl()));

        var teamKey = ecPublicKeyProvider.getEllipticCurvePublicKeyFrom(teamEcdhKey.ecdhKey);

        var generated = sharedKeyProvider.generateSharedKeyBasedOn(teamKey);

        this.sharedKey = generated.SharedKey;
        this.generatedEcdhKey = generated.GeneratedEcdhKey;
    }

    public Object encrypt(Object data) throws NotPossibleToHandleDataTypeException, InvalidCipherTextException, IOException, MandatoryParameterException {
        if (data == null) {
            throw new MandatoryParameterException("data");
        }

        return this.encryptionProvider.encrypt(data);
    }

    public CageRunResult run(String cageName, Object data, boolean async, String version) throws HttpFailureException, IOException, InterruptedException, MandatoryParameterException, NotPossibleToHandleDataTypeException, InvalidCipherTextException, MaxRetryReachedException {
        if (cageName == null || cageName.isEmpty()) {
            throw new MandatoryParameterException("cageName");
        }

        if ( data == null) {
            throw new MandatoryParameterException("data");
        }

        return circuitBreakerProvider.execute(runCageHash, () -> cageExecutionProvider.runCage(getEverVaultRunUrl(), cageName, data, async, version));
    }
}
