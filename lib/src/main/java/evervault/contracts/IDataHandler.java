package evervault.contracts;

import evervault.exceptions.InvalidCipherException;
import evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;

public interface IDataHandler {
    boolean canEncrypt(Object data);
    Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException;
}
