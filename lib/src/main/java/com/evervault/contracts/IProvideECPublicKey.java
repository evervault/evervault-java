package com.evervault.contracts;

import com.evervault.exceptions.NotImplementedException;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public interface IProvideECPublicKey {
    PublicKey getEllipticCurvePublicKeyFrom(String base64key) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, NotImplementedException;
}
