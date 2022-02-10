package evervault.contracts;

import evervault.exceptions.NotImplementedException;
import evervault.models.GeneratedSharedKey;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public interface IProvideSharedKey {
    GeneratedSharedKey generateSharedKeyBasedOn(PublicKey teamCagePublickey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, NotImplementedException;
}
