package com.evervault.crypto;

import com.evervault.utils.Bytes;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.*;
import java.util.Base64;

public class AppPublicKey {

    private PublicKey key;

    private byte[] value;

    public AppPublicKey(String encodedKey) {
        try {
            this.value = Base64.getDecoder().decode(encodedKey);
            this.key = decodeKey(encodedKey);
        } catch (Exception e) {

        }
    }

    private PublicKey decodeKey(String encodedKey) throws NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeySpecException {
        // Step 1: Decode Base64 key to byte array
        byte[] publicKeyByteArray = Base64.getDecoder().decode(encodedKey);

        // Step 2: Get the curve parameters for "secp256r1"
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
        parameters.init(new ECGenParameterSpec("secp256r1"));
        ECParameterSpec ecParameters = parameters.getParameterSpec(ECParameterSpec.class);

        // Step 3: Interpret the public key bytes as an ECPoint.
        // You need to understand the structure of the publicKeyByteArray. Typically, an uncompressed key starts with a 0x04 byte,
        // followed by two 32-byte sequences representing the X and Y coordinates.
        // Ensure your key format corresponds to this structure.
        int coordinateLength = (publicKeyByteArray.length - 1) / 2;
        BigInteger x = new BigInteger(1, Bytes.extract(publicKeyByteArray, 1, coordinateLength));
        BigInteger y = new BigInteger(1, Bytes.extract(publicKeyByteArray, 1 + coordinateLength, coordinateLength));
        ECPoint ecPoint = new ECPoint(x, y);

        // Step 4: Generate the public key
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(ecPoint, ecParameters);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(ecPublicKeySpec);
        return publicKey;
    }

    public PublicKey getKey() {
        return key;
    }

    public byte[] getValue() {
        return this.value;
    }
}