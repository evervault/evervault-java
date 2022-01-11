package EverVault.Contracts;

import org.bouncycastle.crypto.InvalidCipherTextException;

public interface IProvideEncryption {
    String encryptData(String everVaultVersion, DataHeader header, byte[] generatedEcdhKey, byte[] data, byte[] key) throws InvalidCipherTextException;
}
