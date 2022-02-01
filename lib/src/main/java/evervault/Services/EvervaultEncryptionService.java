package evervault.Services;

import evervault.Contracts.IDataHandler;
import evervault.Contracts.IProvideEncryption;
import evervault.DataHandlers.*;

public class EvervaultEncryptionService extends EncryptObjectService {
    public EvervaultEncryptionService(IProvideEncryption encryptionProvider, byte[] generatedEcdhKey, byte[] sharedKey) {
        super(new IDataHandler[] {
                new StringDataHandler(encryptionProvider, generatedEcdhKey, sharedKey),
                new FloatHandler(encryptionProvider, generatedEcdhKey, sharedKey),
                new ShortHandler(encryptionProvider, generatedEcdhKey, sharedKey),
                new BooleanHandler(encryptionProvider, generatedEcdhKey, sharedKey),
                new IntegerHandler(encryptionProvider, generatedEcdhKey, sharedKey),
                new DoubleHandler(encryptionProvider, generatedEcdhKey, sharedKey),
                new LongHandler(encryptionProvider, generatedEcdhKey, sharedKey),
                new ByteHandler(encryptionProvider, generatedEcdhKey, sharedKey),
                new CharHandler(encryptionProvider, generatedEcdhKey, sharedKey),
                new VectorHandler(),
                new MapHandler(),
                new ArrayHandler(),

                /// This has to be the last one, otherwise other structures will not work
                new SerializableHandler(encryptionProvider, generatedEcdhKey, sharedKey)
        });
    }
}
