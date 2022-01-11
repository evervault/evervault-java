package EverVault;

import EverVault.Contracts.DataHeader;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyAgreement;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

public class WhenUsingEncryptionServiceTests {
    private EncryptionService service;

    public WhenUsingEncryptionServiceTests() {
        service = new EncryptionService();
    }

    @Test
    void decodingStringIntoPublicKeyDoesNotThrow() throws NoSuchAlgorithmException, InvalidKeySpecException {
        service.getEllipticCurvePublicKeyFrom("AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr");
    }

    @Test
    void generateSharedKey() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        var publicKey = service.getEllipticCurvePublicKeyFrom("AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr");
        var generated = service.generateSharedKeyBasedOn(publicKey);
        assert generated.SharedKey.length > 0;
    }

    @Test
    void encryptString() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException {
        final String algorithm = "EC";
        final String stdName = "secp256k1";
        final String toEncrypt = "Evervault";

        var provider = new BouncyCastleProvider();
        var keyPairGenerator = KeyPairGenerator.getInstance(algorithm, provider);
        var genParameter = new ECGenParameterSpec(stdName);
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        var keyPair = keyPairGenerator.generateKeyPair();

        var agreement = KeyAgreement.getInstance("ECDH", provider);
        agreement.init(keyPair.getPrivate());
        agreement.doPhase(keyPair.getPublic(), true);

        var sharedKey = agreement.generateSecret();

        var encryptedText = service.encryptData(DataHeader.String, keyPair.getPublic().getEncoded(), toEncrypt.getBytes(StandardCharsets.UTF_8), sharedKey);

        var splitted = encryptedText.split(":");

        assert splitted[0] == "ev";
    }
}
