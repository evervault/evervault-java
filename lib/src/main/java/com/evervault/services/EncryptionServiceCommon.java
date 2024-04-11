package com.evervault.services;

import com.evervault.exceptions.NotImplementedException;
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
        BouncyCastleProvider provider = new BouncyCastleProvider();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(getKeyGeneratorAlgorithm(), provider);
        ECGenParameterSpec genParameter = new ECGenParameterSpec(getCurveName());
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }
}
