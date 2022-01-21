package EverVault.Services;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public abstract class EncryptionServiceCommon extends Base64Handler {
    protected static final String ELLIPTIC_CURVE_ALGORITHM = "EC";

    // Virtual method
    protected String getCurveName() {
        return "";
    }

    protected final BouncyCastleProvider provider;

    public EncryptionServiceCommon() {
        provider = new BouncyCastleProvider();
    }

    protected KeyPair generateNewKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        var provider = new BouncyCastleProvider();
        var keyPairGenerator = KeyPairGenerator.getInstance(ELLIPTIC_CURVE_ALGORITHM, provider);
        var genParameter = new ECGenParameterSpec(getCurveName());
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }
}
