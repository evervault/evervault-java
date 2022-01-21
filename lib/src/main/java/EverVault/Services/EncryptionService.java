package EverVault.Services;

import EverVault.Contracts.*;
import EverVault.Exceptions.InvalidCipherException;
import EverVault.ReadModels.GeneratedSharedKey;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECPublicKeySpec;

import javax.crypto.KeyAgreement;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class EncryptionService extends EncryptionServiceCommon implements IProvideECPublicKey, IProvideSharedKey, IProvideEncryption {
    private static final int DEFAULT_MAC_BIT_SIZE = 128;
    private static final String KEY_AGREEMENT_ALGORITHM = "ECDH";
    private final IProvideEncryptedFormat encryptFormatProvider;

    public EncryptionService(IProvideEncryptedFormat encryptFormatProvider) {
        this.encryptFormatProvider = encryptFormatProvider;
    }

    protected static final String CURVE_NAME_256K1 = "secp256r1";

    @Override
    protected String getCurveName() {
        return CURVE_NAME_256K1;
    }

    @Override
    public PublicKey getEllipticCurvePublicKeyFrom(String base64key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var privateKeyByteArray = decodeBase64String(base64key);

        var spec = ECNamedCurveTable.getParameterSpec(getCurveName());
        var pointQ = spec.getG().multiply(new BigInteger(1, privateKeyByteArray));

        var kf = KeyFactory.getInstance(KEY_AGREEMENT_ALGORITHM, provider);
        var pubSpec = new ECPublicKeySpec(pointQ, spec);

        return kf.generatePublic(pubSpec);
    }

    @Override
    public GeneratedSharedKey generateSharedKeyBasedOn(PublicKey teamCagePublicKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        var keyPair = generateNewKeyPair();

        var agreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM, provider);
        agreement.init(keyPair.getPrivate());
        agreement.doPhase(teamCagePublicKey, true);

        var result = new GeneratedSharedKey();
        result.GeneratedEcdhKey = keyPair.getPublic().getEncoded();
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

        return encryptFormatProvider.format(header, encodeBase64(iv), encodeBase64(generatedEcdhKey), encodeBase64(data));
    }
}
