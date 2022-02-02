package evervault.dataHandlers;

import evervault.contracts.IDataHandler;
import evervault.contracts.IProvideEncryptionForObject;
import evervault.exceptions.InvalidCipherException;
import evervault.exceptions.NotPossibleToHandleDataTypeException;
import java.io.IOException;
import java.util.Vector;

public class ArrayHandler implements IDataHandler {
    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Object[];
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var itemList = new Vector<>();

        for (var item: (Object[]) data) {
            itemList.add(context.encrypt(item));
        }

        return itemList.toArray();
    }
}
