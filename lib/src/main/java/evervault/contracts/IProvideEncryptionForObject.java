package evervault.contracts;

import evervault.exceptions.InvalidCipherException;
import evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;

public interface IProvideEncryptionForObject {
    Object encrypt(Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException;
}
