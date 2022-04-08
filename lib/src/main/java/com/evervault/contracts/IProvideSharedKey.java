package com.evervault.contracts;

import com.evervault.models.GeneratedSharedKey;
import com.evervault.exceptions.Asn1EncodingException;
import com.evervault.exceptions.NotImplementedException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public interface IProvideSharedKey {
    GeneratedSharedKey generateSharedKeyBasedOn(PublicKey teamCagePublickey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, NotImplementedException, Asn1EncodingException;
}
