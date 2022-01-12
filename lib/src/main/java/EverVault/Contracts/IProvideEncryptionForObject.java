package EverVault.Contracts;

import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.IOException;

public interface IProvideEncryptionForObject {
    Object encrypt(Object data) throws InvalidCipherTextException, NotPossibleToHandleDataTypeException, IOException;
}
