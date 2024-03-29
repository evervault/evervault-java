package com.evervault;

import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IProvideEncryption;
import com.evervault.contracts.IProvideEncryptionForObject;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;
import com.evervault.services.EvervaultEncryptionService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Vector;

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

    private TestSetup getService() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotImplementedException {
        var setup = new EncryptSetup();

        var encryptionProvider = mock(IProvideEncryption.class);

        var testSetup = new TestSetup();
        var key = setup.keyPair.getPublic().getEncoded();
        testSetup.encryptionService = new EvervaultEncryptionService(encryptionProvider, key, setup.sharedKey, setup.cageKey);
        testSetup.encryptionProvider = encryptionProvider;

        return testSetup;
    }

    @Test
    void handlesStringCorrectly() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var testSetup = getService();

        var someString = "Foo";
        var someReturn = "Bar";

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.String), any(), eq(someString.getBytes(StandardCharsets.UTF_8)), any(), any())).thenReturn(someReturn);

        assert testSetup.encryptionService.encrypt(someString).equals(someReturn);
    }

    @Test
    void ifNotAbleToHandleTypeThenThrows() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotImplementedException {
        var testSetup = getService();

        assertThrows(NotPossibleToHandleDataTypeException.class, () -> testSetup.encryptionService.encrypt(null));
    }

    @Test
    void handlesDictionaryCorrectly() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var testSetup = getService();

        var map = new HashMap<String, String>();
        map.put("Foo", "Bar");
        map.put("Ever", "Vault");

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.String), any(), eq("Bar".getBytes(StandardCharsets.UTF_8)), any(), any())).thenReturn("Foo");
        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.String), any(), eq("Vault".getBytes(StandardCharsets.UTF_8)), any(), any())).thenReturn("Ever");

        var encrypted = (HashMap<String, String>) testSetup.encryptionService.encrypt(map);

        assert "Foo".equals(encrypted.get("Foo"));
        assert "Ever".equals(encrypted.get("Ever"));
    }

    @Test
    void handlesBooleanCorrectly() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var testSetup = getService();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Boolean), any(), eq("true".getBytes(StandardCharsets.UTF_8)), any(), any())).thenReturn("true");
        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Boolean), any(), eq("false".getBytes(StandardCharsets.UTF_8)), any(), any())).thenReturn("false");

        assert "true".equals(testSetup.encryptionService.encrypt(true));
        assert "false".equals(testSetup.encryptionService.encrypt(false));
    }

    @Test
    void handlesIntegerCorrectly() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var testSetup = getService();

        int someInt = 132;
        var bytes = ByteBuffer.allocate(4).putInt(someInt).array();
        final String result = "onetwothree";

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytes), any(), any())).thenReturn(result);

        assert result.equals(testSetup.encryptionService.encrypt(someInt));
    }

    @Test
    void handlesByteCorrectly() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var testSetup = getService();

        final byte someByte = 1;
        final String result = "onetwothree";

        var bytes = ByteBuffer.allocate(1).put(someByte).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.String), any(), eq(bytes), any(), any())).thenReturn(result);

        assert result.equals(testSetup.encryptionService.encrypt(someByte));
    }

    @Test
    void handlesShortCorrectly() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var testSetup = getService();

        final short someShort = 1;
        final String result = "onetwothree";
        var bytes = ByteBuffer.allocate(2).putShort(someShort).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytes), any(), any())).thenReturn(result);

        assert result.equals(testSetup.encryptionService.encrypt(someShort));
    }

    @Test
    void handlesLongCorrectly() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var testSetup = getService();

        final long someLong = 1;
        final String result = "onetwothree";
        var bytes = ByteBuffer.allocate(8).putLong(someLong).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytes), any(), any())).thenReturn(result);

        assert result.equals(testSetup.encryptionService.encrypt(someLong));
    }

    @Test
    void handlesFloatCorrectly() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var testSetup = getService();

        final float someFloat = 1;
        final String result = "onetwothree";
        var bytes = ByteBuffer.allocate(4).putFloat(someFloat).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytes), any(), any())).thenReturn(result);

        assert result.equals(testSetup.encryptionService.encrypt(someFloat));
    }

    @Test
    void handlesDoubleCorrectly() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var testSetup = getService();

        final double someDouble = 1;
        final String result = "onetwothree";
        var bytes = ByteBuffer.allocate(8).putDouble(someDouble).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytes), any(), any())).thenReturn(result);

        assert result.equals(testSetup.encryptionService.encrypt(someDouble));
    }

    @Test
    void handlesCharCorrectly() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var testSetup = getService();

        final char someChar = 'a';
        final String result = "onetwothree";
        var bytes = ByteBuffer.allocate(2).putChar(someChar).array();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.String), any(), eq(bytes), any(), any())).thenReturn(result);

        var content = (String)testSetup.encryptionService.encrypt(someChar);

        assert result.equals(content);
    }

    @Test
    void handlesVectorCorrectly() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotPossibleToHandleDataTypeException, IOException, InvalidCipherException {
        var testSetup = getService();

        var list = new Vector<Integer>();
        list.add(1);
        list.add(2);

        var bytesFirstItem = ByteBuffer.allocate(4).putInt(1).array();
        var bytesSecondItem = ByteBuffer.allocate(4).putInt(2).array();
        final var firstTrans = "one";
        final var secTrans = "two";

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytesFirstItem), any(), any())).thenReturn(firstTrans);
        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.Number), any(), eq(bytesSecondItem), any(), any())).thenReturn(secTrans);

        var result = (Vector<String>)testSetup.encryptionService.encrypt(list);

        assert result.get(0).equals(firstTrans);
        assert result.get(1).equals(secTrans);
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

    @Test
    void handlesCustomClass() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, IOException, NotPossibleToHandleDataTypeException, InvalidCipherException {
        var testSetup = getService();

        var someInstance = new SomeClass();
        someInstance.Name = "Foo";

        var outputStream = new ByteArrayOutputStream();
        var objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(someInstance);
        var byteArray = outputStream.toByteArray();

        when(testSetup.encryptionProvider.encryptData(eq(DataHeader.String), any(), eq(byteArray), any(), any())).thenReturn("Bar");

        var encrypted = testSetup.encryptionService.encrypt(someInstance);

        assert encrypted.equals("Bar");
    }

    @Test
    void handlesArrayCorrectly() throws NotImplementedException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, IOException, InvalidCipherException {
        var testSetup = getService();

        var sampleArray = new String[]{
                "Foo",
                "Ever"
        };

        when(testSetup.encryptionProvider.encryptData(any(), any(), eq("Foo".getBytes(StandardCharsets.UTF_8)), any(), any())).thenReturn("Bar");
        when(testSetup.encryptionProvider.encryptData(any(), any(), eq("Ever".getBytes(StandardCharsets.UTF_8)), any(), any())).thenReturn("Vault");

        var encrypted = (Object[])testSetup.encryptionService.encrypt(sampleArray);

        assert "Bar".equals(encrypted[0]);
        assert "Vault".equals(encrypted[1]);
    }
}
