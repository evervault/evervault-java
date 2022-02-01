package evervault.DataHandlers;

import evervault.Contracts.DataHeader;
import evervault.Contracts.IDataHandler;
import evervault.Contracts.IProvideEncryption;
import evervault.Contracts.IProvideEncryptionForObject;
import evervault.Exceptions.InvalidCipherException;
import evervault.Exceptions.NotPossibleToHandleDataTypeException;

import java.nio.ByteBuffer;

public class FloatHandler implements IDataHandler {
    private static final int BUFFER_SIZE = 4;

    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;

    public FloatHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey) {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Float;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, InvalidCipherException {
        var bytes = ByteBuffer.allocate(BUFFER_SIZE).putFloat((float) data).array();

        return encryptionProvider.encryptData(DataHeader.Number, generatedEcdhKey, bytes, sharedKey);
    }
}
