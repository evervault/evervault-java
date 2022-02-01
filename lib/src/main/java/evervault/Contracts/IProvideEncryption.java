package evervault.Contracts;

import evervault.Exceptions.InvalidCipherException;

public interface IProvideEncryption {
    String encryptData(DataHeader header, byte[] generatedEcdhKey, byte[] data, byte[] sharedKey) throws InvalidCipherException;
}
