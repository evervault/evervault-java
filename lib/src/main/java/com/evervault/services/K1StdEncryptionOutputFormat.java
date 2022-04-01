package com.evervault.services;

import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IProvideEncryptedFormat;
import com.evervault.utils.Base64Handler;

import java.nio.charset.StandardCharsets;

public class K1StdEncryptionOutputFormat implements IProvideEncryptedFormat {
    private static final String EVERVAULT_VERSION = "DUB";
    private static final String ENCRYPTED_FIELD_FORMAT = "ev:%s%s:%s:%s:%s:$";
    private final String everVaultVersionToUse;

    public K1StdEncryptionOutputFormat() {
        everVaultVersionToUse = Base64Handler.encodeBase64(EVERVAULT_VERSION.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String format(DataHeader header, String iv, String publicKey, String encryptedPayload) {
        var prefix = "";
        if ( header != DataHeader.String) {
            prefix = String.format(":%s", header.toString());
        }

        return String.format(ENCRYPTED_FIELD_FORMAT, everVaultVersionToUse, prefix, Base64Handler.removePadding(iv), Base64Handler.removePadding(publicKey), Base64Handler.removePadding(encryptedPayload));
    }
}
