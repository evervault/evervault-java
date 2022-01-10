package EverVault;

import EverVault.Contracts.IProvideECPublicKey;
import EverVault.Contracts.IProvideSharedKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;

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
    public byte[] generateSharedKeyBasedOn(PublicKey teamCagePublickey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        var keyPairGenerator = KeyPairGenerator.getInstance(ELLIPTIC_CURVE_ALGORITHM, new BouncyCastleProvider());
        var genParameter = new ECGenParameterSpec(SECP256K1_NAME);
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        var keypair = keyPairGenerator.generateKeyPair();

        var encodedPublicKey = keypair.getPublic().getEncoded();

       // AgreementUtilities
    }
}
