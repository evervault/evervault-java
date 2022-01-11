package EverVault.Contracts;

import org.bouncycastle.crypto.InvalidCipherTextException;

public interface IProvideEncryption {
    String encryptData(DataHeader header, byte[] generatedEcdhKey, byte[] data, byte[] sharedKey) throws InvalidCipherTextException;
}
