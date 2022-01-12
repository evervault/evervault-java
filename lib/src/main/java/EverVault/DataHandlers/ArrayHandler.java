package EverVault.DataHandlers;

import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.util.Vector;

public class ArrayHandler implements IDataHandler {
    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Object[];
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var itemList = new Vector<>();

        for (var item: (Object[]) data) {
            itemList.add(context.encrypt(item));
        }

        return itemList.toArray();
    }
}
