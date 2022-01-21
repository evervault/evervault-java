package EverVault;

import EverVault.Exceptions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WhenUsingApiAgainstRealEnvironmentTests {
    private static final String API_ADDRESS = "https://api.evervault.io";
    private static final String RUN_ADDRESS = "https://run.evervault.io";
    private static final String ENV_API_KEY = "ENVIRONMENT_APIKEY";
    private static final String CAGE_NAME = "javasdktest";

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
        assertThrows(IllegalArgumentException.class, () -> new EverVault(getEnvironmentApiKey(), "notanurl", "notanurl", false));
    }

    @Test
    void doesThrowWhenInvalidKey() {
        assertThrows(HttpFailureException.class, () -> new EverVault("foo", API_ADDRESS, RUN_ADDRESS, false));
    }

    @Test
    void encryptSomeDataCorrectly() throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException, InvalidCipherException, MandatoryParameterException {
        final String someDataToEncrypt = "Foo";

        var everVault = new EverVault(getEnvironmentApiKey(), API_ADDRESS, RUN_ADDRESS, false);

        var result = (String) everVault.encrypt(someDataToEncrypt);

        assert !result.isEmpty();
        assert !result.isBlank();

        var split = result.split(":");
        assertEquals(6, split.length);
    }

    private static class Bar {
        public String name;
    }

    @Test
    void encryptAndRun() throws NotPossibleToHandleDataTypeException, InvalidCipherException, IOException, MandatoryParameterException, HttpFailureException, InvalidAlgorithmParameterException, MaxRetryReachedException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException {
        var everVault = new EverVault(getEnvironmentApiKey(), API_ADDRESS, RUN_ADDRESS, false);

        var foo = new Bar();
        foo.name = (String) everVault.encrypt("Foo");

        var cageResult = everVault.run(CAGE_NAME, foo, false, null);

        assert !cageResult.runId.isEmpty();
    }
}