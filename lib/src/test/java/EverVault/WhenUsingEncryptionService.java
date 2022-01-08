package EverVault;

import org.junit.jupiter.api.Test;

public class WhenUsingEncryptionService {
    @Test
    void decodingStringIntoPublicKeyGetsCorrectResult() {
        var service = new EncryptionService();
        service.GetEllipticCurvePublicKeyFromEncodedString("AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr");

        // TODO: not sure what to assert for the key :(
    }
}
