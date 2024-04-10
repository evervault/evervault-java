package com.evervault.crypto;

import com.evervault.utils.Bytes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

class KeyDerivationSession {

    private PublicKey ephemeralPublicKey;
    private byte[] sharedSecret;

    public KeyDerivationSession(PublicKey ephemeralPublicKey, byte[] sharedSecret) {
        this.sharedSecret = sharedSecret;
        this.ephemeralPublicKey = ephemeralPublicKey;
    }

    public byte[] performDerivation() throws NoSuchAlgorithmException {
        byte[] padding = {0x00, 0x00, 0x00, 0x01};
        byte[] encodedPublicKey = ephemeralPublicKey.getEncoded(); // DER-Encoded Public Key
        byte[] concat = Bytes.concat(sharedSecret, padding, encodedPublicKey);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        return messageDigest.digest(concat);
    }

}
