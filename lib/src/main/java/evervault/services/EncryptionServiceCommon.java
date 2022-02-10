package evervault.services;

import evervault.exceptions.NotImplementedException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public abstract class EncryptionServiceCommon {
    protected static final String ELLIPTIC_CURVE_ALGORITHM = "EC";

    // Virtual method
    protected String getCurveName() throws NotImplementedException {
        throw new NotImplementedException("getCurveName");
    }

    // Virtual method
    protected String getKeyGeneratorAlgorithm() throws NotImplementedException {
        throw new NotImplementedException("getKeyGeneratorAlgorithm");
    }

    protected final BouncyCastleProvider provider;

    public EncryptionServiceCommon() {
        provider = new BouncyCastleProvider();
    }

    protected KeyPair generateNewKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NotImplementedException {
        var provider = new BouncyCastleProvider();
        var keyPairGenerator = KeyPairGenerator.getInstance(getKeyGeneratorAlgorithm(), provider);
        var genParameter = new ECGenParameterSpec(getCurveName());
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }
}
