package com.evervault.dataHandlers;

import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IDataHandler;
import com.evervault.contracts.IProvideEncryptionForObject;
import com.evervault.contracts.IProvideEncryption;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

public class CharHandler implements IDataHandler {
    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;
    private final PublicKey teamPublicKey;

    public CharHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey, PublicKey teamPublicKey) {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
        this.teamPublicKey = teamPublicKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Character;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, InvalidCipherException, NotImplementedException {
        char original = (char) data;
        String formattedData = String.valueOf(original);
        return encryptionProvider.encryptData(DataHeader.String, generatedEcdhKey, formattedData.getBytes(StandardCharsets.UTF_8), sharedKey, teamPublicKey);
    }
}
