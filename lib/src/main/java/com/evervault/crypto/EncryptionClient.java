package com.evervault.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class EncryptionClient {

    private AppPublicKey publicKey;
    private KeyPair ephemeralKeyPair;

    EncryptionClient(AppPublicKey publicKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        // ev:key:1:4Hh2WCo2QjhXV5xDPPCXYXxKeP5N9BlwL9PgP9AyCo60Kfs4fcDM6dblC7ws8Huu4:sZ4zvj:E4EIKl
        if (publicKey == null) {
            throw new IllegalArgumentException("The argument `publicKey` cannot be null.");
        }
        this.publicKey = publicKey;
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        kpg.initialize(ecSpec, new SecureRandom());
        KeyPair ephemeralKeyPair = kpg.generateKeyPair();

        KeyAgreementSession kas = new KeyAgreementSession(publicKey.getKey(), ephemeralKeyPair.getPrivate());
        byte[] shareSecret = kas.performKeyAgreement();
        KeyDerivationSession kds = new KeyDerivationSession(ephemeralKeyPair.getPublic(), shareSecret);
        byte[] derivedSecret = kds.performDerivation();
        var random = new SecureRandom();
        var iv = new byte[12];
        random.nextBytes(iv);
        byte[] plaintext = "hello world!".getBytes("utf-8");

        SecretKeySpec keySpec = new SecretKeySpec(derivedSecret, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        cipher.updateAAD(publicKey.getValue());

        byte[] ciphertext = cipher.doFinal(plaintext);
        String encodedEphemeralPublicKey = new String(Base64.getUrlEncoder().encode(ephemeralKeyPair.getPublic().getEncoded()), "utf-8");
        String encodedCipherText = new String(Base64.getUrlEncoder().encode(ciphertext), "utf-8");
        String encodedKeyIv = new String(Base64.getUrlEncoder().encode(iv), "utf-8");

        String result = "ev:Tk9D:" + encodedKeyIv + ":" + encodedEphemeralPublicKey + ":" + encodedCipherText + ":$";
        System.out.println(result);
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
