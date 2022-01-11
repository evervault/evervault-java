package EverVault.Contracts;

import org.bouncycastle.crypto.InvalidCipherTextException;

public interface IDataHandler {
    boolean canEncrypt(Object data);
    String encrypt(Object data) throws InvalidCipherTextException;
}
