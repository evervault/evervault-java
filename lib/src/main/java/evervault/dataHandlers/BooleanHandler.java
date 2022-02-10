package evervault.dataHandlers;

import evervault.contracts.DataHeader;
import evervault.contracts.IDataHandler;
import evervault.contracts.IProvideEncryption;
import evervault.contracts.IProvideEncryptionForObject;
import evervault.exceptions.InvalidCipherException;
import evervault.exceptions.NotPossibleToHandleDataTypeException;

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
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, InvalidCipherException {
        var original = (boolean) data;
        var byteToEncrypt = original ? (byte)1 : (byte)0;

        return encryptionProvider.encryptData(DataHeader.Boolean, generatedEcdhKey, new byte[] { byteToEncrypt }, sharedKey);
    }
}
