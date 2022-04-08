package com.evervault.services;

import com.evervault.contracts.IDataHandler;
import com.evervault.contracts.IProvideEncryptionForObject;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;
import java.io.IOException;

public class EncryptObjectService implements IProvideEncryptionForObject {
    private final IDataHandler[] dataHandlers;

    public EncryptObjectService(IDataHandler[] dataHandlers) {
        this.dataHandlers = dataHandlers;
    }

    @Override
    public Object encrypt(Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException, NotImplementedException {
        for (var handler: dataHandlers) {
            if (handler.canEncrypt(data)){
                return handler.encrypt(this, data);
            }
        }

       throw new NotPossibleToHandleDataTypeException();
    }
}
