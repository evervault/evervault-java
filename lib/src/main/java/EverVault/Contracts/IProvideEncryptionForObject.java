package EverVault.Contracts;

import EverVault.Exceptions.InvalidCipherException;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;

public interface IProvideEncryptionForObject {
    Object encrypt(Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException;
}
