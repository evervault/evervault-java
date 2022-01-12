package EverVault.DataHandlers;

import EverVault.Contracts.DataHeader;
import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryption;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableHandler implements IDataHandler {
    private static final int BUFFER_SIZE = 8;

    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;

    public SerializableHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey) {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Serializable;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherTextException, NotPossibleToHandleDataTypeException, IOException {
        var content = (Serializable)data;

        var outputStream = new ByteArrayOutputStream();
        var objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(content);

        var byteArray = outputStream.toByteArray();

        return encryptionProvider.encryptData(DataHeader.String, generatedEcdhKey, byteArray, sharedKey);
    }
}
