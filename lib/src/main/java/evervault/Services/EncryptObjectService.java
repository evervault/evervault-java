package evervault.Services;

import evervault.Contracts.IDataHandler;
import evervault.Contracts.IProvideEncryptionForObject;
import evervault.Exceptions.InvalidCipherException;
import evervault.Exceptions.NotPossibleToHandleDataTypeException;
import java.io.IOException;

public class EncryptObjectService implements IProvideEncryptionForObject {
    private final IDataHandler[] dataHandlers;

    public EncryptObjectService(IDataHandler[] dataHandlers) {
        this.dataHandlers = dataHandlers;
    }

    @Override
    public Object encrypt(Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        for (var handler: dataHandlers) {
            if (handler.canEncrypt(data)){
                return handler.encrypt(this, data);
            }
        }

       throw new NotPossibleToHandleDataTypeException();
    }
}
