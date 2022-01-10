package EverVault.Contracts;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public interface IProvideECPublicKey {
    PublicKey getEllipticCurvePublicKeyFrom(byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException;

}
