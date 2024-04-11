package com.evervault.dataHandlers;

import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IDataHandler;
import com.evervault.contracts.IProvideEncryptionForObject;
import com.evervault.contracts.IProvideEncryption;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

public class FloatHandler implements IDataHandler {
    private static final int BUFFER_SIZE = 4;

    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;
    private PublicKey teamPublicKey;

    public FloatHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey, PublicKey teamPublicKey) {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
        this.teamPublicKey = teamPublicKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Float;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, InvalidCipherException, NotImplementedException {
        float original = (float) data;
        String formattedData = String.valueOf(original);
        return encryptionProvider.encryptData(DataHeader.Number, generatedEcdhKey, formattedData.getBytes(StandardCharsets.UTF_8), sharedKey, teamPublicKey);
    }
}
