package EverVault;

import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryptionForObject;

public class EncryptObjectService implements IProvideEncryptionForObject {
    private final IDataHandler[] dataHandlers;

    public EncryptObjectService(IDataHandler[] dataHandlers) {
        this.dataHandlers = dataHandlers;
    }

    @Override
    public String Encrypt(byte[] generatedEcdhKey, byte[] sharedKey, Object data) {
        //        for ( int i = 0; i < this.dataHandlers.length; i++) {
//            if (this.dataHandlers[i].canEncrypt(data)) {
//
//            }
//        }

        return null;
    }
}
