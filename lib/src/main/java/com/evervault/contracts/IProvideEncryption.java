package com.evervault.contracts;

import java.security.PublicKey;

import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;

public interface IProvideEncryption {
    String encryptData(DataHeader header, byte[] generatedEcdhKey, byte[] data, byte[] sharedKey, PublicKey teamPublicKey) throws InvalidCipherException, NotImplementedException;
}
