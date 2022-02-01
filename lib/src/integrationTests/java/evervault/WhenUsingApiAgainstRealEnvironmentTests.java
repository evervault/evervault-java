package evervault;

import evervault.Exceptions.*;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WhenUsingApiAgainstRealEnvironmentTests {
    private static final String API_ADDRESS = "https://api.evervault.com";
    private static final String RUN_ADDRESS = "https://run.evervault.com";
    private static final String ENV_API_KEY = "ENVIRONMENT_API_KEY";
    private static final String CAGE_NAME = "hello-cage-bad-emphasis";

    private String getEnvironmentApiKey() {
        return System.getenv(ENV_API_KEY);
    }

    @Test
    void weHaveEnvironmentSetupProperly() {
        var envContent = getEnvironmentApiKey();

        assert !envContent.isEmpty();
        assert !envContent.isBlank();
    }

    @Test
    void doesThrowWhenUrlIsNotValid() {
        assertThrows(IllegalArgumentException.class, () -> new Evervault(getEnvironmentApiKey(), "notanurl", "notanurl"));
    }

    @Test
    void doesThrowWhenInvalidKey() {
        assertThrows(HttpFailureException.class, () -> new Evervault("foo", API_ADDRESS, RUN_ADDRESS));
    }

    @Test
    void encryptSomeDataCorrectly() throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException, InvalidCipherException, MandatoryParameterException {
        final String someDataToEncrypt = "Foo";

        var evervault = new Evervault(getEnvironmentApiKey(), API_ADDRESS, RUN_ADDRESS);

        var result = (String) evervault.encrypt(someDataToEncrypt);

        assert !result.isEmpty();
        assert !result.isBlank();

        var split = result.split(":");
        assertEquals(6, split.length);
    }

    private static class Bar {
        public String name;

        public static final String NAME_CONTENT = "Foo";

        public static Bar createFooStructure(Evervault evervault) throws NotPossibleToHandleDataTypeException, InvalidCipherException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, MandatoryParameterException, InvalidKeyException {
            var bar = new Bar();
            bar.name = (String) evervault.encrypt(NAME_CONTENT);

            return bar;
        }
    }

    @Test
    void encryptAndRun() throws NotPossibleToHandleDataTypeException, InvalidCipherException, IOException, MandatoryParameterException, HttpFailureException, InvalidAlgorithmParameterException, MaxRetryReachedException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException {
        var evervault = new Evervault(getEnvironmentApiKey(), API_ADDRESS, RUN_ADDRESS);

        var cageResult = evervault.run(CAGE_NAME, Bar.createFooStructure(evervault), false, null);

        assert !cageResult.runId.isEmpty();
    }

    private static class OwnEvervault extends Evervault {
        public OwnEvervault(String apiKey, String everVaultApiUrl, String everVaultRunUrl) throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException {
            super(apiKey, everVaultApiUrl, everVaultRunUrl);
        }

        public byte[] getSharedKey() {
            return this.sharedKey;
        }
    }

    private static final int ENCRYPTED_DATA_SPLIT_POSITION = 4;
    private static final int IV_POS = 2;

    @Test
    void decryptDataWorksAsExpected() throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException, InvalidCipherException, MandatoryParameterException, InvalidCipherTextException {
        var evervault = new OwnEvervault(getEnvironmentApiKey(), API_ADDRESS, RUN_ADDRESS);

        var bar = Bar.createFooStructure(evervault);

        var splitContent = bar.name.split(":");

        var key = evervault.getSharedKey();

        var decoder = Base64.getDecoder();
        var encryptedData = decoder.decode(splitContent[ENCRYPTED_DATA_SPLIT_POSITION]);
        var iv = decoder.decode(splitContent[IV_POS]);

        assertEquals(12, iv.length);

        var parameters = new AEADParameters(new KeyParameter(key), 128, iv);
        var cipher = new GCMBlockCipher(new AESEngine());
        cipher.init(false, parameters);

        var output = new byte[3];

        var len = cipher.processBytes(encryptedData, 0, encryptedData.length, output, 0);

        cipher.doFinal(output, len);

        var result = new String(output, StandardCharsets.US_ASCII);
        assert result.equals(Bar.NAME_CONTENT);
    }
}