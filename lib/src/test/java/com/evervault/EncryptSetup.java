package com.evervault;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class EncryptSetup {
    private final String ALGORITHM = "EC";
    private final String STDNAME = "secp256r1";
    private final String KEYAGREEMENT_ALGORITHM = "ECDH";

    public KeyPair keyPair;
    public byte[] sharedKey;
    public PublicKey cageKey;

    public EncryptSetup() throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, provider);
        ECGenParameterSpec genParameter = new ECGenParameterSpec(STDNAME);
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        keyPair = keyPairGenerator.generateKeyPair();

        KeyAgreement agreement = KeyAgreement.getInstance(KEYAGREEMENT_ALGORITHM, provider);
        agreement.init(keyPair.getPrivate());
        agreement.doPhase(keyPair.getPublic(), true);

        sharedKey = agreement.generateSecret();
        cageKey = keyPair.getPublic();
    }
}
