package EverVault;

import EverVault.Contracts.IEncryptionService;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionService implements IEncryptionService {
    public ECPublicKeySpec GetEllipticCurvePublicKeyFromEncodedString(String encodedKey) {
        var decoder = Base64.getDecoder();
        var decodedKey = decoder.decode(encodedKey.getBytes(StandardCharsets.UTF_8));
        var secP256K1Curve = new SecP256K1Curve();
        var secP256K1Point = secP256K1Curve.decodePoint(decodedKey);
        var ecParameterSpec = new ECParameterSpec(secP256K1Curve, secP256K1Point, secP256K1Curve.getCofactor());
        return new ECPublicKeySpec(secP256K1Point, ecParameterSpec);
    }

    public void Generate() {
        var secP256K1Curve = new SecP256K1Curve();
        var domainParameters = new ECDomainParameters(secP256K1Curve, secP256K1Curve.getInfinity(), secP256K1Curve.getCofactor());
        var keyParameters = new ECKeyGenerationParameters(domainParameters, new SecureRandom());
        var generator = new ECKeyPairGenerator();
        generator.init(keyParameters);
        var pair = generator.generateKeyPair();
        var publicKey = pair.getPublic();
    }
}
