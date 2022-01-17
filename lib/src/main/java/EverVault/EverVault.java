package EverVault;

import EverVault.Exceptions.HttpFailureException;
import EverVault.Services.*;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public final class EverVault extends EverVaultService {
    private static final String EVERVAULT_BASE_URL = "https://api.evervault.com/";
    private static final String EVERVAULT_RUN_URL = "https://run.evervault.com/";

    public String getEverVaultBaseUrl() {
        return EVERVAULT_BASE_URL;
    }

    public String getEverVaultRunUrl() {
        return EVERVAULT_RUN_URL;
    }

    public EverVault(String apiKey) throws HttpFailureException, InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InterruptedException {
        var httpHandler = new HttpHandler(apiKey);
        var encryptService = new EncryptionService(new StdEncryptionOutputFormat());

        this.setupCageExecutionProvider(httpHandler);

        this.setupKeyProviders(httpHandler, encryptService, encryptService);

        var encryptForObject = new EverVaultEncryptionService(encryptService, this.generatedEcdhKey, this.sharedKey);

        this.setupEncryption(encryptForObject);
    }
}
