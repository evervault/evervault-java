package EverVault;

import EverVault.Contracts.DataHeader;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.KeyAgreement;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import java.util.stream.Stream;

public class WhenUsingEncryptionServiceTests {
    private final EncryptionService service;

    public WhenUsingEncryptionServiceTests() {
        service = new EncryptionService();
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
    void encryptStringProvidesCorrectContent() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, InvalidCipherTextException {
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

        assert Objects.equals(splitted[0], "ev");
    }

    @ParameterizedTest
    @MethodSource("formattingParameters")
    void formattingEncryptedDataMustReturnDataInCorrectFormat(String expectedResult, DataHeader header, String iv, String publicKey, String payLoad) {
        assert service.format(header, iv, publicKey, payLoad).equals(expectedResult);
    }

    static Stream<Arguments> formattingParameters() {
        return Stream.of(
                Arguments.of("ev:EV:PK:PL:$", DataHeader.String, "IV", "PK", "PL"),
                Arguments.of("ev:boolean:EV:PK:PL:$", DataHeader.Boolean, "IV", "PK", "PL"),
                Arguments.of("ev:number:EV:PK:PL:$", DataHeader.Number, "IV", "PK", "PL"),
                Arguments.of("ev:EV:PK:PL:$", DataHeader.String, "IV====", "PK==", "PL===="),
                Arguments.of("ev:boolean:EV:PK:PL:$", DataHeader.Boolean, "IV==", "PK=", "PL"),
                Arguments.of("ev:number:EV:PK:PL:$", DataHeader.Number, "IV==", "PK=", "PL=========="));
    }
}
