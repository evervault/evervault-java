package com.evervault;

import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IProvideEncryptedFormat;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;
import com.evervault.services.EncryptionService;
import com.evervault.services.EncryptionServiceBasedOnCurve256K1;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public final class WhenUsingEncryptionServiceTests {
    private  static final String SECP256K1_NAME = "secp256k1";
    private static final String ALGORITHM_TO_MATCH = "ECDH";
    private final EncryptionService service;
    private final IProvideEncryptedFormat encryptFormatProvider;

    private final String KeySample = "AxmQTyNImujntQtwasywz2YqGWWN3AwuZZ5LnL9lF8ug";

    public WhenUsingEncryptionServiceTests() {
        encryptFormatProvider = mock(IProvideEncryptedFormat.class);
        service = new EncryptionServiceBasedOnCurve256K1(encryptFormatProvider);
    }

    private static class WithoutAlgorithm extends EncryptionService {
        public WithoutAlgorithm(IProvideEncryptedFormat encryptFormatProvider) {
            super(encryptFormatProvider);
        }

        @Override
        protected String getCurveName() {
            return "secp256k1";
        }
    }

    private static class WithoutCurve extends EncryptionService {
        public WithoutCurve(IProvideEncryptedFormat encryptFormatProvider) {
            super(encryptFormatProvider);
        }

        @Override
        protected String getKeyGeneratorAlgorithm() throws NotImplementedException {
            return "ECDH";
        }
    }

    @Test
    void notDefiningCurveThrows() {
        var es = new WithoutCurve(encryptFormatProvider);
        assertThrows(NotImplementedException.class, () -> es.getEllipticCurvePublicKeyFrom(KeySample));
    }

    @Test
    void notDefiningAlgorithmThrows() {
        var es = new WithoutAlgorithm(encryptFormatProvider);
        assertThrows(NotImplementedException.class, () -> es.getEllipticCurvePublicKeyFrom(KeySample));
    }

    @Test
    void decodingStringIntoPublicKeyDoesNotThrow() throws NoSuchAlgorithmException, InvalidKeySpecException, NotImplementedException {
        service.getEllipticCurvePublicKeyFrom(KeySample);
    }

    @Test
    void decodedPublicKeyMustMatchAlgorithm() throws NoSuchAlgorithmException, InvalidKeySpecException, NotImplementedException {

        var key = service.getEllipticCurvePublicKeyFrom(KeySample);
        var algo = key.getAlgorithm();
        assert ALGORITHM_TO_MATCH.equals(algo);
    }

    @Test
    void generateSharedKeyWithDecodedStringDoesNotThrow() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, NotImplementedException {
        var publicKey = service.getEllipticCurvePublicKeyFrom(KeySample);
        service.generateSharedKeyBasedOn(publicKey);
    }

    @Test
    void generateSharedKeyDoesNotThrow() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotImplementedException {
        var provider = new BouncyCastleProvider();
        var keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_TO_MATCH, provider);
        var genParameter = new ECGenParameterSpec(SECP256K1_NAME);
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        var keyPair = keyPairGenerator.generateKeyPair();

        var generated = service.generateSharedKeyBasedOn(keyPair.getPublic());
        assert generated.SharedKey.length > 0;
    }

    @Test
    void encryptStringReturnsOutputOfFormat() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidCipherException {
        var setup = new EncryptSetup();
        final String result = "Foo";

        when(encryptFormatProvider.format(any(), any(), any(), any())).thenReturn(result);

        assert service.encryptData(DataHeader.String, setup.keyPair.getPublic().getEncoded(), "SomeData".getBytes(StandardCharsets.UTF_8), setup.sharedKey).equals(result);
    }

    @Test
    void encryptStringProvidesCorrectContent() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherException {
        final String result = "Foo";

        var setup = new EncryptSetup();
        var dataHeaderTypeCapture = ArgumentCaptor.forClass(DataHeader.class);
        var ivCapture = ArgumentCaptor.forClass(String.class);
        var publicKeyCapture = ArgumentCaptor.forClass(String.class);
        var encryptedPayloadCapture = ArgumentCaptor.forClass(String.class);

        when(encryptFormatProvider.format(any(), any(), any(), any())).thenReturn(result);

        service.encryptData(DataHeader.String, setup.keyPair.getPublic().getEncoded(), "SomeData".getBytes(StandardCharsets.UTF_8), setup.sharedKey);

        verify(encryptFormatProvider, times(1)).format(dataHeaderTypeCapture.capture(), ivCapture.capture(), publicKeyCapture.capture(), encryptedPayloadCapture.capture());

        assert dataHeaderTypeCapture.getValue().equals(DataHeader.String);
    }
}
