package com.evervault.crypto;

import java.util.Base64;

public class PublicKey {

    private byte[] value;
    private EcdhCurve curve;

    public PublicKey(String key, EcdhCurve curve) {
        this.value = Base64.getDecoder().decode(key); // throws IllegalArgumentException
        this.curve = curve;
    }

}