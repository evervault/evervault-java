package EverVault;

import EverVault.Contracts.IProvideECPublicKey;
import EverVault.Contracts.IProvideSharedKey;
import EverVault.ReadModels.GeneratedSharedKey;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

public class EncryptionService implements IProvideECPublicKey, IProvideSharedKey {
    private static final String ELLIPTIC_CURVE_ALGORITHM = "EC";
    private static final String SECP256K1_NAME = "secp256k1";

    @Override
    public PublicKey getEllipticCurvePublicKeyFrom(byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var curve = new SecP256K1Curve();
        var point = curve.decodePoint(key);
        var parameterSpec = new ECParameterSpec(curve, point, curve.getOrder());
        var spec = new ECPublicKeySpec(point, parameterSpec);

        return KeyFactory.getInstance(ELLIPTIC_CURVE_ALGORITHM, new BouncyCastleProvider()).generatePublic(spec);
    }

    @Override
    public GeneratedSharedKey generateSharedKeyBasedOn(PublicKey teamCagePublickey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        var provider = new BouncyCastleProvider();
        var keyPairGenerator = KeyPairGenerator.getInstance(ELLIPTIC_CURVE_ALGORITHM, provider);
        var genParameter = new ECGenParameterSpec(SECP256K1_NAME);
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        var keyPair = keyPairGenerator.generateKeyPair();

        var agreement = KeyAgreement.getInstance("ECDH", provider);
        agreement.init(keyPair.getPrivate());
        agreement.doPhase(keyPair.getPublic(), true);

        var result = new GeneratedSharedKey();
        result.GeneratedEcdhKey = keyPair.getPublic().getEncoded();
        result.SharedKey = agreement.generateSecret();

        return result;
    }

    public void Whatever() {
        var cipher = new GCMBlockCipher(new AESEngine());
    }
}
