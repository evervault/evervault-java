package EverVault;

import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryption;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenEncryptingDifferentTypesOfDataTests {
    @Test
    void handlesStringCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException {
        var setup = new EncryptSetup();
        var encryptionProvider = mock(IProvideEncryption.class);

        var encryptObjectService = new EncryptObjectService(new IDataHandler[] {
                new StringDataHandler(encryptionProvider)
        });

        var someString = "Foo";
        var someReturn = "Bar";

        when(encryptionProvider.encryptData(any(), any(), any(), any())).thenReturn(someReturn);

        assert encryptObjectService.Encrypt(setup.keyPair.getPublic().getEncoded(), setup.sharedKey, someString).equals(someReturn);
    }
}
