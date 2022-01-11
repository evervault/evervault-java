package EverVault;

import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryption;
import org.junit.jupiter.api.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.mockito.Mockito.mock;

public class WhenEncryptingDifferentTypesOfDataTests {
    @Test
    void handlesStringCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
        var setup = new EncryptSetup();
        var encryptionProvider = mock(IProvideEncryption.class);

        var encryptObjectService = new EncryptObjectService(new IDataHandler[] {
                new StringDataHandler(encryptionProvider)
        });

        var someString = "Foo";

        encryptObjectService.Encrypt(setup.keyPair.getPublic().getEncoded(), setup.sharedKey, someString);
    }
}
