package EverVault;

import EverVault.Contracts.IEncryptionService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class EncryptionService implements IEncryptionService {
    private static final String ELLIPTIC_CURVE_ALGORITHM = "EC";

    @Override
    public PublicKey GetEllipticCurvePublicKeyFrom(byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var curve = new SecP256K1Curve();
        var point = curve.decodePoint(key);
        var parameterSpec = new ECParameterSpec(curve, point, curve.getOrder());
        var spec = new ECPublicKeySpec(point, parameterSpec);

        return KeyFactory.getInstance(ELLIPTIC_CURVE_ALGORITHM, new BouncyCastleProvider()).generatePublic(spec);
    }

//    public void Generate() {
//        var secP256K1Curve = new SecP256K1Curve();
//        var domainParameters = new ECDomainParameters(secP256K1Curve, secP256K1Curve.getInfinity(), secP256K1Curve.getCofactor());
//        var keyParameters = new ECKeyGenerationParameters(domainParameters, new SecureRandom());
//        var generator = new ECKeyPairGenerator();
//        generator.init(keyParameters);
//        var pair = generator.generateKeyPair();
//        var publicKey = pair.getPublic();
//    }
}
