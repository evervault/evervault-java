package EverVault;

import EverVault.Services.EncryptionServiceCommon;
import org.junit.jupiter.api.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class WhenGeneratingAKeyPairTests {
    private static class SomeNewService extends EncryptionServiceCommon {
        public KeyPair newKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
            return this.generateNewKeyPair();
        }
    }

    private static final String ELLIPTIC_CURVE_ALGORITHM = "EC";

    @Test
    void keyPairAlgorithmMustMatch() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        var service = new SomeNewService();

        var keypair = service.newKeyPair();

        assert ELLIPTIC_CURVE_ALGORITHM.equals(keypair.getPublic().getAlgorithm());
    }
}
