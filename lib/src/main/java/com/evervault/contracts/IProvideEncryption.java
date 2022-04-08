package com.evervault.contracts;

import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;

public interface IProvideEncryption {
    String encryptData(DataHeader header, byte[] generatedEcdhKey, byte[] data, byte[] sharedKey, byte[] teamPublicKey) throws InvalidCipherException, NotImplementedException;
}
