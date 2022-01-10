package EverVault.Contracts;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public interface IProvideSharedKey {
    byte[] generateSharedKeyBasedOn(PublicKey teamCagePublickey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
}
