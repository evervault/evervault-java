package com.evervault.crypto;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;

class KeyAgreementSession {

    private PublicKey appPublicKey;
    private PrivateKey ephemeralPrivateKey;

    public KeyAgreementSession(PublicKey appPublicKey, PrivateKey ephemeralPrivateKey) {
        this.appPublicKey = appPublicKey;
        this.ephemeralPrivateKey = ephemeralPrivateKey;
    }

    public byte[] performKeyAgreement() throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement ecdh = KeyAgreement.getInstance("ECDH");
        ecdh.init(ephemeralPrivateKey);
        ecdh.doPhase(appPublicKey, true);
        return ecdh.generateSecret();
    }

}
