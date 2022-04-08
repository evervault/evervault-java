package com.evervault.services;

import com.evervault.contracts.IDataHandler;
import com.evervault.contracts.IProvideEncryption;
import com.evervault.dataHandlers.*;

public class EvervaultEncryptionService extends EncryptObjectService {
    public EvervaultEncryptionService(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey, byte[] teamPublicKey) {
        super(new IDataHandler[] {
                new StringDataHandler(encryptionProvider, generatedEcdhKey, sharedKey, teamPublicKey),
                new FloatHandler(encryptionProvider, generatedEcdhKey, sharedKey, teamPublicKey),
                new ShortHandler(encryptionProvider, generatedEcdhKey, sharedKey, teamPublicKey),
                new BooleanHandler(encryptionProvider, generatedEcdhKey, sharedKey, teamPublicKey),
                new IntegerHandler(encryptionProvider, generatedEcdhKey, sharedKey, teamPublicKey),
                new DoubleHandler(encryptionProvider, generatedEcdhKey, sharedKey, teamPublicKey),
                new LongHandler(encryptionProvider, generatedEcdhKey, sharedKey, teamPublicKey),
                new ByteHandler(encryptionProvider, generatedEcdhKey, sharedKey, teamPublicKey),
                new CharHandler(encryptionProvider, generatedEcdhKey, sharedKey, teamPublicKey),
                new VectorHandler(),
                new MapHandler(),
                new ArrayHandler(),

                /// This has to be the last one, otherwise other structures will not work
                new SerializableHandler(encryptionProvider, generatedEcdhKey, sharedKey, teamPublicKey)
        });
    }
}
