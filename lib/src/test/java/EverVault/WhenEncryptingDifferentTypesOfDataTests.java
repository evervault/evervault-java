package EverVault;

import EverVault.Contracts.DataHeader;
import EverVault.Contracts.IProvideEncryption;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        testSetup.encryptionService = new EverVaultEncryptionService(encryptionProvider, key, setup.sharedKey);
        testSetup.encryptionProvider = encryptionProvider;

        return testSetup;
    }

    @Test
    void handlesStringCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var testSetup = getService();

        var someString = "Foo";
        var someReturn = "Bar";

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.String), any(), eq(someString.getBytes(StandardCharsets.UTF_8)), any())).thenReturn(someReturn);

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

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.String), any(), eq("Bar".getBytes(StandardCharsets.UTF_8)), any())).thenReturn("Foo");
        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.String), any(), eq("Vault".getBytes(StandardCharsets.UTF_8)), any())).thenReturn("Ever");

        var encrypted = (HashMap<String, String>) testSetup.encryptionService.encrypt(map);

        assert "Foo".equals(encrypted.get("Foo"));
        assert "Ever".equals(encrypted.get("Ever"));
    }

    @Test
    void handlesBooleanCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, InvalidCipherTextException {
        var testSetup = getService();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Boolean), any(), eq(new byte[]{1}), any())).thenReturn("true");
        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Boolean), any(), eq(new byte[]{0}), any())).thenReturn("false");

        assert "true".equals(testSetup.encryptionService.encrypt(true));
        assert "false".equals(testSetup.encryptionService.encrypt(false));
    }

    @Test
    void handlesIntegerCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var testSetup = getService();

        int someInt = 132;
        var bytes = ByteBuffer.allocate(4).putInt(someInt).array();
        final String result = "onetwothree";

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytes), any())).thenReturn(result);

        assert result.equals(testSetup.encryptionService.encrypt(someInt));
    }

    @Test
    void handlesByteCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var testSetup = getService();

        final byte someByte = 1;
        final String result = "onetwothree";

        var bytes = ByteBuffer.allocate(1).put(someByte).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.String), any(), eq(bytes), any())).thenReturn(result);

        assert result.equals(testSetup.encryptionService.encrypt(someByte));
    }

    @Test
    void handlesShortCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var testSetup = getService();

        final short someShort = 1;
        final String result = "onetwothree";
        var bytes = ByteBuffer.allocate(2).putShort(someShort).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytes), any())).thenReturn(result);

        assert result.equals(testSetup.encryptionService.encrypt(someShort));
    }

    @Test
    void handlesLongCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var testSetup = getService();

        final long someLong = 1;
        final String result = "onetwothree";
        var bytes = ByteBuffer.allocate(8).putLong(someLong).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytes), any())).thenReturn(result);

        assert result.equals(testSetup.encryptionService.encrypt(someLong));
    }

    @Test
    void handlesFloatCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var testSetup = getService();

        final float someFloat = 1;
        final String result = "onetwothree";
        var bytes = ByteBuffer.allocate(4).putFloat(someFloat).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytes), any())).thenReturn(result);


        assert result.equals(testSetup.encryptionService.encrypt(someFloat));
    }

    @Test
    void handlesDoubleCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException, NotPossibleToHandleDataTypeException {
        var testSetup = getService();

        final double someDouble = 1;
        final String result = "onetwothree";
        var bytes = ByteBuffer.allocate(8).putDouble(someDouble).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytes), any())).thenReturn(result);

        assert result.equals(testSetup.encryptionService.encrypt(someDouble));
    }

    @Test
    void handlesCharCorrectly() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException {
        var testSetup = getService();

        final char someChar = 'a';
        final String result = "onetwothree";
        var bytes = ByteBuffer.allocate(2).putChar(someChar).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.String), any(), eq(bytes), any())).thenReturn(result);
    }

    private static class SomeClass implements Serializable {
        public String Name;

        @Override
        public String toString() {
            return "SomeClass{" +
                    "Name='" + Name + '\'' +
                    '}';
        }
    }

//    @Test
//    void handlesCustomClass() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, IOException {
//        var testSetup = getService();
//
//        var someInstance = new SomeClass();
//        someInstance.Name = "Foo";
//
//        var outputStream = new ByteArrayOutputStream();
//        var objectOutputStream = new ObjectOutputStream(outputStream);
//        objectOutputStream.write(someInstance);
//
//
//        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytes), any())).thenReturn(result);when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(new byte[] { 0 }), any())).thenReturn("false");
//
//    }

    @Test
    void handlesArrayCorrectly() throws NotPossibleToHandleDataTypeException, InvalidCipherTextException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
        var testSetup = getService();

        var sampleArray = new String[]{
                "Foo",
                "Ever"
        };

        when(testSetup.encryptionProvider.encryptData(any(), any(), eq("Foo".getBytes(StandardCharsets.UTF_8)), any())).thenReturn("Bar");
        when(testSetup.encryptionProvider.encryptData(any(), any(), eq("Ever".getBytes(StandardCharsets.UTF_8)), any())).thenReturn("Vault");

        var encrypted = (String[]) testSetup.encryptionService.encrypt(sampleArray);

        assert "Bar".equals(encrypted[0]);
        assert "Vault".equals(encrypted[1]);
    }
}
