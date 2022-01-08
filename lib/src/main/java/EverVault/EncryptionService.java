package EverVault;

import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionService {
    public ECPublicKeySpec GetEllipticCurvePublicKeyFromEncodedString(String encodedKey) {
        var decoder = Base64.getDecoder();
        var decodedKey = decoder.decode(encodedKey.getBytes(StandardCharsets.UTF_8));
        var secP256K1Curve = new SecP256K1Curve();
        var secP256K1Point = secP256K1Curve.decodePoint(decodedKey);
        var ecParameterSpec = new ECParameterSpec(secP256K1Curve, secP256K1Point, BigInteger.TEN);
        return new ECPublicKeySpec(secP256K1Point, ecParameterSpec);
    }
}
