package EverVault;

import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.IOException;

public class EncryptObjectService implements IProvideEncryptionForObject {
    private final IDataHandler[] dataHandlers;

    public EncryptObjectService(IDataHandler[] dataHandlers) {
        this.dataHandlers = dataHandlers;
    }

    @Override
    public Object encrypt(Object data) throws InvalidCipherTextException, NotPossibleToHandleDataTypeException, IOException {
        for (var handler: dataHandlers) {
            if (handler.canEncrypt(data)){
                return handler.encrypt(this, data);
            }
        }

       throw new NotPossibleToHandleDataTypeException();
    }
}
