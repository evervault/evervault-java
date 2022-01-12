package EverVault.DataHandlers;

import EverVault.Contracts.DataHeader;
import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryption;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.nio.ByteBuffer;

public class BooleanHandler implements IDataHandler {
    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;

    public BooleanHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey) {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Boolean;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var original = (boolean) data;
        var byteToEncrypt = original ? (byte)1 : (byte)0;

        return encryptionProvider.encryptData(DataHeader.Boolean, generatedEcdhKey, new byte[] { byteToEncrypt }, sharedKey);
    }
}
