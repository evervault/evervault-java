package evervault.DataHandlers;

import evervault.Contracts.DataHeader;
import evervault.Contracts.IDataHandler;
import evervault.Contracts.IProvideEncryption;
import evervault.Contracts.IProvideEncryptionForObject;
import evervault.Exceptions.InvalidCipherException;
import evervault.Exceptions.NotPossibleToHandleDataTypeException;

public class ByteHandler implements IDataHandler {
    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;

    public ByteHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey) {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Byte;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, InvalidCipherException {
        return encryptionProvider.encryptData(DataHeader.String, generatedEcdhKey, new byte[] { (Byte)data }, sharedKey);
    }
}
