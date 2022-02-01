package evervault.Contracts;

import evervault.Exceptions.InvalidCipherException;
import evervault.Exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;

public interface IProvideEncryptionForObject {
    Object encrypt(Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException;
}
