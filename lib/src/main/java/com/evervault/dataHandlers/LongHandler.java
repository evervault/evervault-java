package com.evervault.dataHandlers;

import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IProvideEncryptionForObject;
import com.evervault.contracts.IDataHandler;
import com.evervault.contracts.IProvideEncryption;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.nio.ByteBuffer;

public class LongHandler implements IDataHandler {
    private static final int BUFFER_SIZE = 8;

    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;

    public LongHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey) {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Long;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, InvalidCipherException {
        var bytes = ByteBuffer.allocate(BUFFER_SIZE).putLong((long) data).array();

        return encryptionProvider.encryptData(DataHeader.Number, generatedEcdhKey, bytes, sharedKey);
    }
}