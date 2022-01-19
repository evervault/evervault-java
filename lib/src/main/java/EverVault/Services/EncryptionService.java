package EverVault.Services;

import EverVault.Contracts.*;
import EverVault.ReadModels.GeneratedSharedKey;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.custom.sec.SecP256R1Curve;

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

    @Override
    public PublicKey getEllipticCurvePublicKeyFrom(String base64key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var key = decodeBase64String(base64key);
        var curve = new SecP256R1Curve();
        var point = curve.decodePoint(key);

        var parameterSpec = new ECParameterSpec(curve, point, curve.getOrder());
        var spec = new ECPublicKeySpec(point, parameterSpec);
        return KeyFactory.getInstance(ELLIPTIC_CURVE_ALGORITHM, getProvider()).generatePublic(spec);
    }

    @Override
    public GeneratedSharedKey generateSharedKeyBasedOn(PublicKey teamCagePublicKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        var keyPair = generateNewKeyPair();

        var agreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM, getProvider());
        agreement.init(keyPair.getPrivate());
        agreement.doPhase(teamCagePublicKey, true);

        var result = new GeneratedSharedKey();
        result.GeneratedEcdhKey = keyPair.getPublic().getEncoded();
        result.SharedKey = agreement.generateSecret();

        return result;
    }

    @Override
    public String encryptData(DataHeader header, byte[] generatedEcdhKey, byte[] data, byte[] sharedKey) throws InvalidCipherTextException {
        var random = new SecureRandom();
        var iv = new byte[12];
        random.nextBytes(iv);

        var cipher = new GCMBlockCipher(new AESEngine());
        var parameters = new AEADParameters(new KeyParameter(sharedKey), DEFAULT_MAC_BIT_SIZE, iv);
        cipher.init(true, parameters);

        var cipherText = new byte[cipher.getOutputSize(data.length)];
        var len = cipher.processBytes(data, 0, data.length, cipherText, 0);
        cipher.doFinal(cipherText, len);

        return encryptFormatProvider.format(header, encodeBase64(iv), encodeBase64(generatedEcdhKey), encodeBase64(data));
    }
}
