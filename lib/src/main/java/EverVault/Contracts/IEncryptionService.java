package EverVault.Contracts;

import org.bouncycastle.jce.spec.ECPublicKeySpec;

public interface IEncryptionService {
    ECPublicKeySpec GetEllipticCurvePublicKeyFromEncodedString(String encodedKey);
}
