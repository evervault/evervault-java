package com.evervault.services;

import com.evervault.contracts.*;
import com.evervault.models.GeneratedSharedKey;
import com.evervault.models.Secp256r1Constants;
import com.evervault.utils.Base64Handler;
import com.evervault.exceptions.Asn1EncodingException;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;
import com.evervault.utils.EcdhCurve;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import com.google.common.primitives.Bytes;

import java.security.*;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.*;

public abstract class EncryptionService extends EncryptionServiceCommon implements IProvideECPublicKey, IProvideSharedKey, IProvideEncryption {
    protected static final int DEFAULT_MAC_BIT_SIZE = 128;
    protected static final String KEY_AGREEMENT_ALGORITHM = "ECDH";
    protected final IProvideEncryptedFormat encryptFormatProvider;

    public EncryptionService(IProvideEncryptedFormat encryptFormatProvider) {
        this.encryptFormatProvider = encryptFormatProvider;
    }

    protected String getKeyAgreementAlgorithm() throws NotImplementedException {
        throw new NotImplementedException("getKeyAgreementAlgorithm");
    }

    protected boolean isNistCurve(String curveName) {
        return EcdhCurve.SECP256R1.equalValue(curveName);
    }

    @Override
    public PublicKey getEllipticCurvePublicKeyFrom(String base64key) throws NoSuchAlgorithmException, InvalidKeySpecException, NotImplementedException {
        var publicKeyByteArray = Base64Handler.decodeBase64String(base64key);

        var spec = ECNamedCurveTable.getParameterSpec(getCurveName());
        var publicKey = new ECPublicKeySpec(spec.getCurve().decodePoint(publicKeyByteArray), spec);

        var kf = KeyFactory.getInstance(getKeyAgreementAlgorithm(), provider);

        return kf.generatePublic(publicKey);
    }

    @Override
    public GeneratedSharedKey generateSharedKeyBasedOn(PublicKey teamCagePublicKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, NotImplementedException, Asn1EncodingException {
        var isNistCurve = this.isNistCurve(getCurveName());
        var keyPair = generateNewKeyPair();

        var agreement = KeyAgreement.getInstance(getKeyAgreementAlgorithm(), provider);
        agreement.init(keyPair.getPrivate());
        agreement.doPhase(teamCagePublicKey, true);

        var generatedPublicKey = ((BCECPublicKey) keyPair.getPublic()).getQ().getEncoded(true);
        var secret =  agreement.generateSecret();
        
        if (!isNistCurve) {
            var result = new GeneratedSharedKey();
            result.GeneratedEcdhKey = generatedPublicKey;
            result.SharedKey = secret;
            return result;
        }

        byte[] padding = {0x00, 0x00, 0x00, 0x01};
        var uncompressedKey = ((BCECPublicKey) keyPair.getPublic()).getQ().getEncoded(false);

        var derEncoder = new DEREncoder(new Secp256r1Constants());
        byte[] encodedPublicKey;
        try {
            encodedPublicKey = derEncoder.publicKeyToDer(uncompressedKey);
        } catch (Exception e) {
            throw new Asn1EncodingException();
        };

        byte[] concatSecret = Bytes.concat(secret, padding, encodedPublicKey);
        
        var sha256 = new SHA256Digest();
        byte[] hash = new byte[sha256.getDigestSize()];
        sha256.update(concatSecret, 0, concatSecret.length);
        sha256.doFinal(hash, 0);
    
        var result = new GeneratedSharedKey();
        result.SharedKey = hash;
        result.GeneratedEcdhKey = generatedPublicKey;
        return result;
    }

    @Override
    public String encryptData(DataHeader header, byte[] generatedEcdhKey, byte[] data, byte[] sharedKey, PublicKey teamPublicKey) throws InvalidCipherException, NotImplementedException {
        var isNistCurve = this.isNistCurve(getCurveName());
        var compressedTeamPublicKey = ((BCECPublicKey) teamPublicKey).getQ().getEncoded(true);
        var random = new SecureRandom();
        var iv = new byte[12];
        random.nextBytes(iv);

        var cipher = new GCMBlockCipher(new AESEngine());
        AEADParameters parameters;
        if (!isNistCurve) {
            parameters = new AEADParameters(new KeyParameter(sharedKey), DEFAULT_MAC_BIT_SIZE, iv);
        } else {
            parameters = new AEADParameters(new KeyParameter(sharedKey), DEFAULT_MAC_BIT_SIZE, iv, compressedTeamPublicKey);
        }
        cipher.init(true, parameters);

        var cipherText = new byte[cipher.getOutputSize(data.length)];
        var len = cipher.processBytes(data, 0, data.length, cipherText, 0);

        try {
            cipher.doFinal(cipherText, len);
        } catch (InvalidCipherTextException e) {
            // We don't want to expose Bouncy Castle to the user.
            throw new InvalidCipherException(e);
        }

        var formatted = encryptFormatProvider.format(
            header,
            Base64Handler.encodeBase64(iv),
            Base64Handler.encodeBase64(generatedEcdhKey),
            Base64Handler.encodeBase64(cipherText)
        );

        return formatted;
    }
}
