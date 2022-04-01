package com.evervault.dataHandlers;

import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IDataHandler;
import com.evervault.contracts.IProvideEncryptionForObject;
import com.evervault.contracts.IProvideEncryption;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;

import java.nio.ByteBuffer;

public class CharHandler implements IDataHandler {
    /// Size of char in java is 16bit unicode
    private static final int BUFFER_SIZE = 2;

    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;

    public CharHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey) {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Character;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, InvalidCipherException {
        var bytes = ByteBuffer.allocate(BUFFER_SIZE).putChar((char) data).array();

        return encryptionProvider.encryptData(DataHeader.String, generatedEcdhKey, bytes, sharedKey);
    }
}
