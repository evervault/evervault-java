package EverVault.Contracts;

import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

public interface IProvideEncryptionForObject {
    Object encrypt(Object data) throws InvalidCipherTextException, NotPossibleToHandleDataTypeException;
}
