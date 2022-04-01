package com.evervault.contracts;

import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;

public interface IProvideEncryptionForObject {
    Object encrypt(Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException;
}
