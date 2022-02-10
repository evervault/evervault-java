package evervault.dataHandlers;

import evervault.contracts.IDataHandler;
import evervault.contracts.IProvideEncryptionForObject;
import evervault.exceptions.InvalidCipherException;
import evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.io.IOException;
import java.util.Vector;

public class VectorHandler implements IDataHandler {
    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Vector<?>;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var content = (Vector<?>)data;
        var result = new Vector<>();

        for (var item :
                content) {
            result.add(context.encrypt(item));
        }

        return result;
    }
}
