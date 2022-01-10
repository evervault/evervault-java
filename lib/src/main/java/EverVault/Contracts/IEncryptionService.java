package EverVault.Contracts;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public interface IEncryptionService {
    PublicKey GetEllipticCurvePublicKeyFrom(byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
