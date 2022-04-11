package com.evervault.dataHandlers;

import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IProvideEncryptionForObject;
import com.evervault.contracts.IDataHandler;
import com.evervault.contracts.IProvideEncryption;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

public class StringDataHandler implements IDataHandler {
    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;
    private PublicKey teamPublicKey;

    public StringDataHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey, PublicKey teamPublicKey)
    {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
        this.teamPublicKey = teamPublicKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof String;
    }

    @Override
    public String encrypt(IProvideEncryptionForObject context, Object data) throws InvalidCipherException, NotImplementedException {
        return encryptionProvider.encryptData(DataHeader.String, generatedEcdhKey, ((String)data).getBytes(StandardCharsets.UTF_8), sharedKey, teamPublicKey);
    }
}
