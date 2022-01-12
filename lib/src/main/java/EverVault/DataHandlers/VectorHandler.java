package EverVault.DataHandlers;

import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.util.Vector;

public class VectorHandler implements IDataHandler {
    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Vector<?>;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var content = (Vector<?>)data;
        var result = new Vector<>();

        for (var item :
                content) {
            result.add(context.encrypt(item));
        }

        return result;
    }
}
