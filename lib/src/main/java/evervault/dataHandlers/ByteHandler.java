package evervault.dataHandlers;

import evervault.contracts.DataHeader;
import evervault.contracts.IDataHandler;
import evervault.contracts.IProvideEncryption;
import evervault.contracts.IProvideEncryptionForObject;
import evervault.exceptions.InvalidCipherException;
import evervault.exceptions.NotPossibleToHandleDataTypeException;

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
