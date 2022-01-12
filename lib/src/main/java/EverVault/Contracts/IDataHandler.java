package EverVault.Contracts;

import org.bouncycastle.crypto.InvalidCipherTextException;

public interface IDataHandler {
    boolean canEncrypt(Object data);
    Object encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherTextException;
}
