package evervault.contracts;

import evervault.exceptions.InvalidCipherException;

public interface IProvideEncryption {
    String encryptData(DataHeader header, byte[] generatedEcdhKey, byte[] data, byte[] sharedKey) throws InvalidCipherException;
}
