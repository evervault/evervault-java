package EverVault;

import EverVault.Contracts.DataHeader;
import EverVault.Contracts.IProvideEncryptedFormat;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WhenUsingEncryptionServiceTests {
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
    void generateSharedKeyDoesNotThrow() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        var publicKey = service.getEllipticCurvePublicKeyFrom("AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr");
        var generated = service.generateSharedKeyBasedOn(publicKey);
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
