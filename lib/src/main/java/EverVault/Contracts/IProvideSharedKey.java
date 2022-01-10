package EverVault.Contracts;

import EverVault.ReadModels.GeneratedSharedKey;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public interface IProvideSharedKey {
    GeneratedSharedKey generateSharedKeyBasedOn(PublicKey teamCagePublickey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException;
}
