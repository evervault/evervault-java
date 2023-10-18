package com.evervault.crypto;

import java.security.KeyPair;

public class EncryptionService {

    private PublicKey publicKey;
    private KeyPair ephemeralKeyPair;
    private

    public EncryptionService(PublicKey publicKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("The argument publicKey cannot be null.");
        }
        this.publicKey = publicKey;
    }

    public String encrypt(boolean bool) {
        return "";
    }
    
    public String encrypt(int integer) {
        return "";
    }

    public String encrypt(double decimal) {
        return "";
    }

    public String encrypt(String string) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("Null or an empty String cannot be encrypted");
        }
        return "";
    }
}
