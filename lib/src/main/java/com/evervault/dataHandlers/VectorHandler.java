package com.evervault.dataHandlers;

import com.evervault.contracts.IProvideEncryptionForObject;
import com.evervault.contracts.IDataHandler;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;
import java.util.Vector;

public class VectorHandler implements IDataHandler {
    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Vector<?>;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException, NotImplementedException {
        Vector<?> content = (Vector<?>)data;
        Vector<Object> result = new Vector<>();

        for (Object item :
                content) {
            result.add(context.encrypt(item));
        }

        return result;
    }
}
