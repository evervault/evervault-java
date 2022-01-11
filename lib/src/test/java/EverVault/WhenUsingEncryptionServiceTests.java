package EverVault;

import EverVault.Contracts.DataHeader;
import EverVault.Contracts.IProvideEncryptedFormat;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.crypto.KeyAgreement;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WhenUsingEncryptionServiceTests {
    private final EncryptionService service;
    private final IProvideEncryptedFormat encryptFormatProvider;

    private static class EncryptSetup {
        private final String ALGORITHM = "EC";
        private final String STDNAME = "secp256k1";
        private final String KEYAGREEMENT_ALGORITHM = "ECDH";

        public KeyPair keyPair;
        public byte[] sharedKey;

        public EncryptSetup() throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException {
            var provider = new BouncyCastleProvider();
            var keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, provider);
            var genParameter = new ECGenParameterSpec(STDNAME);
            keyPairGenerator.initialize(genParameter, new SecureRandom());
            keyPair = keyPairGenerator.generateKeyPair();

            var agreement = KeyAgreement.getInstance(KEYAGREEMENT_ALGORITHM, provider);
            agreement.init(keyPair.getPrivate());
            agreement.doPhase(keyPair.getPublic(), true);

            sharedKey = agreement.generateSecret();
        }
    }

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

        when(encryptFormatProvider.format(any(), any(), any(), any(), any())).thenReturn(result);

        assert service.encryptData("DUB", DataHeader.String, setup.keyPair.getPublic().getEncoded(), "SomeData".getBytes(StandardCharsets.UTF_8), setup.sharedKey).equals(result);
    }

    @Test
    void encryptStringProvidesCorrectContent() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException {
        final String result = "Foo";

        var setup = new EncryptSetup();
        var everVaultVersionCapture = ArgumentCaptor.forClass(String.class);
        var dataHeaderTypeCapture = ArgumentCaptor.forClass(DataHeader.class);
        var ivCapture = ArgumentCaptor.forClass(String.class);
        var publicKeyCapture = ArgumentCaptor.forClass(String.class);
        var encryptedPayloadCapture = ArgumentCaptor.forClass(String.class);

        when(encryptFormatProvider.format(any(), any(), any(), any(), any())).thenReturn(result);

        service.encryptData("DUB", DataHeader.String, setup.keyPair.getPublic().getEncoded(), "SomeData".getBytes(StandardCharsets.UTF_8), setup.sharedKey);

        verify(encryptFormatProvider, times(1)).format(everVaultVersionCapture.capture(), dataHeaderTypeCapture.capture(), ivCapture.capture(), publicKeyCapture.capture(), encryptedPayloadCapture.capture());

        var encoder = Base64.getEncoder();

        var version = new String(encoder.encode("DUB".getBytes(StandardCharsets.UTF_8)), StandardCharsets.US_ASCII);

        assert everVaultVersionCapture.getValue().equals(version);
        assert dataHeaderTypeCapture.getValue().equals(DataHeader.String);
    }
}
