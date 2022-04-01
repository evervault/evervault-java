package com.evervault.dataHandlers;

import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IProvideEncryptionForObject;
import com.evervault.contracts.IDataHandler;
import com.evervault.contracts.IProvideEncryption;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;

public class ByteHandler implements IDataHandler {
    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;

    public ByteHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey) {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Byte;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, InvalidCipherException {
        return encryptionProvider.encryptData(DataHeader.String, generatedEcdhKey, new byte[] { (Byte)data }, sharedKey);
    }
}
