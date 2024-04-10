package com.evervault.services;

import com.evervault.utils.Base64Handler;
import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IProvideEncryptedFormat;

import java.nio.charset.StandardCharsets;

public class R1StdEncryptionOutputFormat implements IProvideEncryptedFormat {
    private static final String EVERVAULT_VERSION = "NOC";
    private static final String ENCRYPTED_FIELD_FORMAT = "ev:%s%s:%s:%s:%s:$";
    private final String evervaultVersionToUse;

    public R1StdEncryptionOutputFormat() {
        evervaultVersionToUse = Base64Handler.encodeBase64(EVERVAULT_VERSION.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String format(DataHeader header, String iv, String publicKey, String encryptedPayload) {
        String prefix = "";
        if ( header != DataHeader.String) {
            prefix = String.format(":%s", header.toString());
        }

        return String.format(ENCRYPTED_FIELD_FORMAT, evervaultVersionToUse, prefix, Base64Handler.removePadding(iv), Base64Handler.removePadding(publicKey), Base64Handler.removePadding(encryptedPayload));
    }
}

