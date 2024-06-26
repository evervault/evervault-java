package com.evervault;

import com.evervault.contracts.DataHeader;
import com.evervault.contracts.IProvideEncryptedFormat;
import com.evervault.exceptions.Asn1EncodingException;
import com.evervault.exceptions.InvalidCipherException;
import com.evervault.exceptions.NotImplementedException;
import com.evervault.models.GeneratedSharedKey;
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
        WithoutCurve es = new WithoutCurve(encryptFormatProvider);
        assertThrows(NotImplementedException.class, () -> es.getEllipticCurvePublicKeyFrom(KeySample));
    }

    @Test
    void notDefiningAlgorithmThrows() {
        WithoutAlgorithm es = new WithoutAlgorithm(encryptFormatProvider);
        assertThrows(NotImplementedException.class, () -> es.getEllipticCurvePublicKeyFrom(KeySample));
    }

    @Test
    void decodingStringIntoPublicKeyDoesNotThrow() throws NoSuchAlgorithmException, InvalidKeySpecException, NotImplementedException {
        service.getEllipticCurvePublicKeyFrom(KeySample);
    }

    @Test
    void decodedPublicKeyMustMatchAlgorithm() throws NoSuchAlgorithmException, InvalidKeySpecException, NotImplementedException {

        PublicKey key = service.getEllipticCurvePublicKeyFrom(KeySample);
        String algo = key.getAlgorithm();
        assert ALGORITHM_TO_MATCH.equals(algo);
    }

    @Test
    void generateSharedKeyWithDecodedStringDoesNotThrow() throws Asn1EncodingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, NotImplementedException {
        PublicKey publicKey = service.getEllipticCurvePublicKeyFrom(KeySample);
        service.generateSharedKeyBasedOn(publicKey);
    }

    @Test
    void generateSharedKeyDoesNotThrow() throws Asn1EncodingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotImplementedException {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_TO_MATCH, provider);
        ECGenParameterSpec genParameter = new ECGenParameterSpec(SECP256K1_NAME);
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        GeneratedSharedKey generated = service.generateSharedKeyBasedOn(keyPair.getPublic());
        assert generated.SharedKey.length > 0;
    }

    @Test
    void encryptStringReturnsOutputOfFormat() throws NotImplementedException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidCipherException {
        EncryptSetup setup = new EncryptSetup();
        final String result = "Foo";
        System.out.println(setup.cageKey);
        when(encryptFormatProvider.format(any(), any(), any(), any())).thenReturn(result);

        assert service.encryptData(DataHeader.String, setup.keyPair.getPublic().getEncoded(), "SomeData".getBytes(StandardCharsets.UTF_8), setup.sharedKey, setup.cageKey).equals(result);
    }

    @Test
    void encryptStringProvidesCorrectContent() throws NotImplementedException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherException {
        final String result = "Foo";

        EncryptSetup setup = new EncryptSetup();
        ArgumentCaptor<DataHeader> dataHeaderTypeCapture = ArgumentCaptor.forClass(DataHeader.class);
        ArgumentCaptor<String> ivCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> publicKeyCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> encryptedPayloadCapture = ArgumentCaptor.forClass(String.class);

        when(encryptFormatProvider.format(any(), any(), any(), any())).thenReturn(result);

        service.encryptData(DataHeader.String, setup.keyPair.getPublic().getEncoded(), "SomeData".getBytes(StandardCharsets.UTF_8), setup.sharedKey, setup.cageKey);

        verify(encryptFormatProvider, times(1)).format(dataHeaderTypeCapture.capture(), ivCapture.capture(), publicKeyCapture.capture(), encryptedPayloadCapture.capture());

        assert dataHeaderTypeCapture.getValue().equals(DataHeader.String);
    }
}
