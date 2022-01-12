package EverVault;

import EverVault.Contracts.DataHeader;
import EverVault.Contracts.IDataHandler;
import EverVault.Contracts.IProvideEncryption;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.DataHandlers.MapHandler;
import EverVault.DataHandlers.StringDataHandler;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenEncryptingDifferentTypesOfDataTests {
    private static class TestSetup {
        public IProvideEncryption encryptionProvider;
        public IProvideEncryptionForObject encryptionService;
    }

    private TestSetup getService() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
        var setup = new EncryptSetup();

        var encryptionProvider = mock(IProvideEncryption.class);

        var testSetup = new TestSetup();
        var key = setup.keyPair.getPublic().getEncoded();
        testSetup.encryptionService =  new EncryptObjectService(new IDataHandler[] {
                new StringDataHandler(encryptionProvider, key, setup.sharedKey),
                new MapHandler()
        });
        testSetup.encryptionProvider = encryptionProvider;

        return testSetup;
    }

    @Test
    void handlesStringCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var testSetup = getService();

        var someString = "Foo";
        var someReturn = "Bar";

        when(testSetup.encryptionProvider.encryptData(ArgumentMatchers.eq(DataHeader.String), any(), ArgumentMatchers.eq(someString.getBytes(StandardCharsets.UTF_8)), any())).thenReturn(someReturn);

        assert testSetup.encryptionService.encrypt(someString).equals(someReturn);
    }

    @Test
    void ifNotAbleToHandleTypeThenThrows() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
        var testSetup = getService();

        assertThrows(NotPossibleToHandleDataTypeException.class, () -> testSetup.encryptionService.encrypt(null));
    }

    @Test
    void handlesDictionaryCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var testSetup = getService();

        var map = new HashMap<String, String>();
        map.put("Foo", "Bar");
        map.put("Ever", "Vault");

        for (var item : map.entrySet()) {
            when(testSetup.encryptionProvider.encryptData(ArgumentMatchers.eq(DataHeader.String), any(), ArgumentMatchers.eq(item.getKey().getBytes(StandardCharsets.UTF_8)), any())).thenReturn(item.getValue());
        }

        var result = (HashMap<String, String>)testSetup.encryptionService.encrypt(map);

        for (var item : map.entrySet()) {
            assert item.getValue().equals(result.get(item.getKey()));
        }
    }
}
