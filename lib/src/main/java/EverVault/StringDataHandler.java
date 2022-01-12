package EverVault;

import EverVault.Contracts.DataHeader;
import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryption;
import EverVault.Contracts.IProvideEncryptionForObject;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.nio.charset.StandardCharsets;

public class StringDataHandler implements IDataHandler {
    private IProvideEncryption encryptionProvider;
    private byte[] generatedEcdhKey;
    private byte[] sharedKey;

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
    public String encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherTextException {
        var content = (String)data;
        return encryptionProvider.encryptData(DataHeader.String, generatedEcdhKey, content.getBytes(StandardCharsets.UTF_8), sharedKey);
    }
}
