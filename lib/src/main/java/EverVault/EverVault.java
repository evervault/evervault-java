package EverVault;

import EverVault.Exceptions.HttpFailureException;
import EverVault.Services.*;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class EverVault extends EverVaultService {
    public EverVault(String apiKey) throws HttpFailureException, InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InterruptedException {
        var httpHandler = new HttpHandlerService(apiKey);
        var encryptService = new EncryptionService(new StdEncryptionOutputFormat());

        this.setupKeyProviders(httpHandler, encryptService, encryptService);

        var encryptForObject = new EverVaultEncryptionService(encryptService, this.ecdhKey.getEncoded(), this.sharedKey.SharedKey);

        this.setupEncryption(encryptForObject);
    }
}
