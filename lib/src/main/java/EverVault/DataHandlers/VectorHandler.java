package EverVault.DataHandlers;

import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.Exceptions.InvalidCipherException;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;

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
