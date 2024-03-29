package com.evervault.dataHandlers;

import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IProvideEncryptionForObject;
import com.evervault.contracts.IDataHandler;
import com.evervault.contracts.IProvideEncryption;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PublicKey;

public class SerializableHandler implements IDataHandler {
    private final IProvideEncryption encryptionProvider;
    private final byte[] generatedEcdhKey;
    private final byte[] sharedKey;
    private PublicKey teamPublicKey;

    public SerializableHandler(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey, PublicKey teamPublicKey) {
        this.encryptionProvider = encryptionProvider;
        this.generatedEcdhKey = generatedEcdhKey;
        this.sharedKey = sharedKey;
        this.teamPublicKey = teamPublicKey;
    }

    @Override
    public boolean canEncrypt(Object data) {
        return data instanceof Serializable;
    }

    @Override
    public Object encrypt(IProvideEncryptionForObject context, Object data) throws NotPossibleToHandleDataTypeException, IOException, InvalidCipherException, NotImplementedException {
        var content = (Serializable)data;

        var outputStream = new ByteArrayOutputStream();
        var objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(content);

        var byteArray = outputStream.toByteArray();

        return encryptionProvider.encryptData(DataHeader.String, generatedEcdhKey, byteArray, sharedKey, teamPublicKey);
    }
}
