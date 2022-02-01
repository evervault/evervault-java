package evervault.Services;

import evervault.Contracts.DataHeader;
import evervault.Contracts.IProvideEncryptedFormat;

import java.nio.charset.StandardCharsets;

public class StdEncryptionOutputFormat extends Base64Handler implements IProvideEncryptedFormat {
    private static final String EVERVAULT_VERSION = "DUB";
    private static final String ENCRYPTED_FIELD_FORMAT = "ev:%s%s:%s:%s:%s:$";
    private final String everVaultVersionToUse;

    public StdEncryptionOutputFormat() {
        everVaultVersionToUse = encodeBase64(EVERVAULT_VERSION.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String format(DataHeader header, String iv, String publicKey, String encryptedPayload) {
        var prefix = "";
        if ( header != DataHeader.String) {
            prefix = String.format(":%s", header.toString());
        }

        return String.format(ENCRYPTED_FIELD_FORMAT, everVaultVersionToUse, prefix, removePadding(iv), removePadding(publicKey), removePadding(encryptedPayload));
    }
}
