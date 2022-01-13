package EverVault.Services;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public abstract class EncryptionServiceCommon extends Base64Handler {
    protected static final String ELLIPTIC_CURVE_ALGORITHM = "EC";
    protected  static final String SECP256K1_NAME = "secp256k1";
    private final BouncyCastleProvider provider;

    public EncryptionServiceCommon() {
        provider = new BouncyCastleProvider();
    }

    protected Provider getProvider() {
        return provider;
    }

    protected KeyPair generateNewKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        var provider = new BouncyCastleProvider();
        var keyPairGenerator = KeyPairGenerator.getInstance(ELLIPTIC_CURVE_ALGORITHM, provider);
        var genParameter = new ECGenParameterSpec(SECP256K1_NAME);
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }
}
