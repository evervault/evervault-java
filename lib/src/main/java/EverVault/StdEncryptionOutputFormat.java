package EverVault;

import EverVault.Contracts.DataHeader;
import EverVault.Contracts.IProvideEncryptedFormat;

public class StdEncryptionOutputFormat extends Base64Handler implements IProvideEncryptedFormat {
    private static final String ENCRYPTED_FIELD_FORMAT = "ev:%s%s:%s:%s:%s:$";

    @Override
    public String format(String everVaultVersion, DataHeader header, String iv, String publicKey, String encryptedPayload) {
        var prefix = "";
        if ( header != DataHeader.String) {
            prefix = String.format(":%s", header.toString());
        }

        return String.format(ENCRYPTED_FIELD_FORMAT, everVaultVersion, prefix, removePadding(iv), removePadding(publicKey), removePadding(encryptedPayload));
    }
}
