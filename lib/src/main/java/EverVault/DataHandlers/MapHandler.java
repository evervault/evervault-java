package EverVault.DataHandlers;

import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryption;
import EverVault.Contracts.IProvideEncryptionForObject;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.util.Map;

public class MapHandler implements IDataHandler {
    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;

    public MapHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey)
    {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return false;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherTextException {
        return null;
    }
}
