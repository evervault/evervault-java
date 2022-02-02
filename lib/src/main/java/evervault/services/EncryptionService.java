package evervault.services;

import evervault.contracts.*;
import evervault.exceptions.InvalidCipherException;
import evervault.models.GeneratedSharedKey;
import evervault.utils.Base64Handler;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECPublicKeySpec;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class EncryptionService extends EncryptionServiceCommon implements IProvideECPublicKey, IProvideSharedKey, IProvideEncryption {
    private static final int DEFAULT_MAC_BIT_SIZE = 128;
    private static final String KEY_AGREEMENT_ALGORITHM = "ECDH";
    private final IProvideEncryptedFormat encryptFormatProvider;

    public EncryptionService(IProvideEncryptedFormat encryptFormatProvider) {
        this.encryptFormatProvider = encryptFormatProvider;
    }

    protected static final String CURVE_NAME_256K1 = "secp256k1";

    @Override
    protected String getCurveName() {
        return CURVE_NAME_256K1;
    }

    @Override
    public PublicKey getEllipticCurvePublicKeyFrom(String base64key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var publicKeyByteArray = Base64Handler.decodeBase64String(base64key);

        var spec = ECNamedCurveTable.getParameterSpec(getCurveName());
        var publicKey = new ECPublicKeySpec(spec.getCurve().decodePoint(publicKeyByteArray), spec);

        var kf = KeyFactory.getInstance(KEY_AGREEMENT_ALGORITHM, provider);

        return kf.generatePublic(publicKey);
    }

    @Override
    public GeneratedSharedKey generateSharedKeyBasedOn(PublicKey teamCagePublicKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        var keyPair = generateNewKeyPair();

        var agreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM, provider);
        agreement.init(keyPair.getPrivate());
        agreement.doPhase(teamCagePublicKey, true);

        var result = new GeneratedSharedKey();
        result.GeneratedEcdhKey = ((BCECPublicKey) keyPair.getPublic()).getQ().getEncoded(true);
        result.SharedKey = agreement.generateSecret();

        return result;
    }

    @Override
    public String encryptData(DataHeader header, byte[] generatedEcdhKey, byte[] data, byte[] sharedKey) throws InvalidCipherException {
        var random = new SecureRandom();
        var iv = new byte[12];
        random.nextBytes(iv);

        var cipher = new GCMBlockCipher(new AESEngine());
        var parameters = new AEADParameters(new KeyParameter(sharedKey), DEFAULT_MAC_BIT_SIZE, iv);
        cipher.init(true, parameters);

        var cipherText = new byte[cipher.getOutputSize(data.length)];
        var len = cipher.processBytes(data, 0, data.length, cipherText, 0);

        try {
            cipher.doFinal(cipherText, len);
        } catch (InvalidCipherTextException e) {
            // We don't want to expose Bouncy Castle to the user.
            throw new InvalidCipherException(e);
        }

        return encryptFormatProvider.format(header, Base64Handler.encodeBase64(iv), Base64Handler.encodeBase64(generatedEcdhKey), Base64Handler.encodeBase64(cipherText));
    }
}
