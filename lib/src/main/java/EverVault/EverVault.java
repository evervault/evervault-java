package EverVault;

import EverVault.Exceptions.HttpFailureException;
import EverVault.Services.*;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public final class EverVault extends EverVaultService {
    public EverVault(String apiKey) throws HttpFailureException, InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InterruptedException {
        var httpHandler = new HttpApiRepository(apiKey);
        var encryptService = new EncryptionService(new StdEncryptionOutputFormat());

        this.setupKeyProviders(httpHandler, encryptService, encryptService);

        var encryptForObject = new EverVaultEncryptionService(encryptService, this.generatedEcdhKey, this.sharedKey);

        this.setupEncryption(encryptForObject);
    }
}
