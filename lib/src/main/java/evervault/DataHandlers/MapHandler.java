package evervault.DataHandlers;

import evervault.Contracts.IDataHandler;
import evervault.Contracts.IProvideEncryptionForObject;
import evervault.Exceptions.InvalidCipherException;
import evervault.Exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;
import java.util.Map;

public class MapHandler implements IDataHandler {
    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Map;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var map = (Map)data;

        for (var item :
                map.entrySet()) {
            var value = ((Map.Entry<?, ?>)item).getValue();

            var encryptedContent = context.encrypt(value);
            map.put(((Map.Entry<?, ?>) item).getKey(), encryptedContent);
        }

        return map;
    }
}
