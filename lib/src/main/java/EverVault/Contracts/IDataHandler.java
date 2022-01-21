package EverVault.Contracts;

import EverVault.Exceptions.InvalidCipherException;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;

public interface IDataHandler {
    boolean canEncrypt(Object data);
    Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException;
}
