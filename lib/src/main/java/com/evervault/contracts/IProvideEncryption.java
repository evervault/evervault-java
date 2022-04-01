package com.evervault.contracts;

import com.evervault.exceptions.InvalidCipherException;

public interface IProvideEncryption {
    String encryptData(DataHeader header, byte[] generatedEcdhKey, byte[] data, byte[] sharedKey) throws InvalidCipherException;
}
