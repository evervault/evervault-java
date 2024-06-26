package com.evervault.dataHandlers;

import com.evervault.contracts.IProvideEncryptionForObject;
import com.evervault.contracts.IDataHandler;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;
import java.util.Map;

public class MapHandler implements IDataHandler {
    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Map;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException, NotImplementedException {
        Map map = (Map)data;

        for (Object item :
                map.entrySet()) {
            Object value = ((Map.Entry<?, ?>)item).getValue();

            Object encryptedContent = context.encrypt(value);
            map.put(((Map.Entry<?, ?>) item).getKey(), encryptedContent);
        }

        return map;
    }
}
