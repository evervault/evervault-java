package EverVault;

import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryption;

public class StringDataHandler implements IDataHandler {
    private IProvideEncryption encryptionProvider;

    public StringDataHandler(IProvideEncryption encryptionProvider)
    {
        this.encryptionProvider = encryptionProvider;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof String;
    }

    @Override
    public String encrypt(Object data) {
        return null;
    }
}
