package EverVault.DataHandlers;

import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.util.Map;

public class MapHandler implements IDataHandler {
    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Map;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherTextException, NotPossibleToHandleDataTypeException {
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
