package EverVault.Contracts;

import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.IOException;

public interface IDataHandler {
    boolean canEncrypt(Object data);
    Object encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherTextException, NotPossibleToHandleDataTypeException, IOException;
}
