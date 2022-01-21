package EverVault.DataHandlers;

import EverVault.Contracts.DataHeader;
import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryption;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.Exceptions.InvalidCipherException;

import java.nio.charset.StandardCharsets;

public class StringDataHandler implements IDataHandler {
    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;

    public StringDataHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey)
    {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof String;
    }

    @Override
    public String encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherException {
        return encryptionProvider.encryptData(DataHeader.String, generatedEcdhKey, ((String)data).getBytes(StandardCharsets.UTF_8), sharedKey);
    }
}
