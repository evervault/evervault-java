package EverVault;

import EverVault.Contracts.IProvideECPublicKey;
import EverVault.Contracts.IProvideEncryption;
import EverVault.Contracts.IProvideSharedKey;
import EverVault.Exceptions.HttpFailureException;
import EverVault.Exceptions.MaxRetryReachedException;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import EverVault.Services.*;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public final class EverVault extends EverVaultService {
    private static final String EVERVAULT_BASE_URL = "https://api.evervault.com/";
    private static final String EVERVAULT_RUN_URL = "https://run.evervault.com/";
    private final String everVaultApiUrl;
    private final String everVaultRunUrl;

    public String getEverVaultBaseUrl() {
        return everVaultApiUrl;
    }

    public String getEverVaultRunUrl() {
        return everVaultRunUrl;
    }

    public EverVault(String apiKey, String everVaultApiUrl, String everVaultRunUrl, boolean use256R1Curve) throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException {
        this.everVaultApiUrl = everVaultApiUrl;
        this.everVaultRunUrl = everVaultRunUrl;

        var httpHandler = new HttpHandler(apiKey);
        var encryptService = GetEncryptionService(use256R1Curve);
        var circuitBreaker = new CircuitBreaker();

        this.setupCircuitBreaker(circuitBreaker);
        this.setupCageExecutionProvider(httpHandler);

        this.setupKeyProviders(httpHandler, encryptService, encryptService);

        var encryptForObject = new EverVaultEncryptionService(encryptService, this.generatedEcdhKey, this.sharedKey);

        this.setupEncryption(encryptForObject);
    }

    private interface IProvideEncryptionRole extends IProvideECPublicKey, IProvideSharedKey, IProvideEncryption { }

    private IProvideEncryptionRole GetEncryptionService(boolean use256R1Curve) {
        var outputFormat = new StdEncryptionOutputFormat();

        if ( use256R1Curve) {
            return (IProvideEncryptionRole)new EncryptionServiceBasedOnCurve256R1(outputFormat);
        }

        return (IProvideEncryptionRole)new EncryptionService(outputFormat);
    }

    public EverVault(String apiKey) throws HttpFailureException, InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InterruptedException, NotPossibleToHandleDataTypeException, MaxRetryReachedException, NoSuchProviderException {
        this(apiKey, EVERVAULT_BASE_URL, EVERVAULT_RUN_URL, false);
    }
}
