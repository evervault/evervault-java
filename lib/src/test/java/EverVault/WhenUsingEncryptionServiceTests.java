package EverVault;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class WhenUsingEncryptionServiceTests {
    @Test
    void decodingStringIntoPublicKeyGetsCorrectResult() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var service = new EncryptionService();

        var decoder = Base64.getDecoder();
        var decodedKey = decoder.decode("AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr".getBytes(StandardCharsets.UTF_8));
        var temp = service.GetEllipticCurvePublicKeyFrom(decodedKey);

        // TODO: not sure what to assert for the key :(
    }
}
