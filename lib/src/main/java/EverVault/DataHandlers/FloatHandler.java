package EverVault.DataHandlers;

import EverVault.Contracts.DataHeader;
import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryption;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

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
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var bytes = ByteBuffer.allocate(BUFFER_SIZE).putFloat((float) data).array();

        return encryptionProvider.encryptData(DataHeader.Number, generatedEcdhKey, bytes, sharedKey);
    }
}
