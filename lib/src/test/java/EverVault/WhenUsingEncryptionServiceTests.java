package EverVault;

import EverVault.Contracts.DataHeader;
import EverVault.Contracts.IProvideEncryptedFormat;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public final class WhenUsingEncryptionServiceTests {
    private static final String ELLIPTIC_CURVE_ALGORITHM = "EC";
    private  static final String SECP256K1_NAME = "secp256k1";
    private final EncryptionService service;
    private final IProvideEncryptedFormat encryptFormatProvider;

    public WhenUsingEncryptionServiceTests() {
        encryptFormatProvider = mock(IProvideEncryptedFormat.class);
        service = new EncryptionService(encryptFormatProvider);
    }

    @Test
    void decodingStringIntoPublicKeyDoesNotThrow() throws NoSuchAlgorithmException, InvalidKeySpecException {
        service.getEllipticCurvePublicKeyFrom("AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr");
    }

    @Test
    void generateSharedKeyWithDecodedStringDoesNotThrow() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException {
        var publicKey = service.getEllipticCurvePublicKeyFrom("AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr");
        service.generateSharedKeyBasedOn(publicKey);
    }

    @Test
    void generateSharedKeyDoesNotThrow() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        var provider = new BouncyCastleProvider();
        var keyPairGenerator = KeyPairGenerator.getInstance(ELLIPTIC_CURVE_ALGORITHM, provider);
        var genParameter = new ECGenParameterSpec(SECP256K1_NAME);
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        var keyPair = keyPairGenerator.generateKeyPair();

        var generated = service.generateSharedKeyBasedOn(keyPair.getPublic());
        assert generated.SharedKey.length > 0;
    }

    @Test
    void encryptStringReturnsOutputOfFormat() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidCipherTextException {
        var setup = new EncryptSetup();
        final String result = "Foo";

        when(encryptFormatProvider.format(any(), any(), any(), any())).thenReturn(result);

        assert service.encryptData(DataHeader.String, setup.keyPair.getPublic().getEncoded(), "SomeData".getBytes(StandardCharsets.UTF_8), setup.sharedKey).equals(result);
    }

    @Test
    void encryptStringProvidesCorrectContent() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException {
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
