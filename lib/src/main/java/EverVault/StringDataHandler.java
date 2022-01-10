package EverVault;

import EverVault.Contracts.IDataHandler;

public class StringDataHandler implements IDataHandler {
    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof String;
    }

    @Override
    public String encrypt(Object data) {
        return null;
    }
}
