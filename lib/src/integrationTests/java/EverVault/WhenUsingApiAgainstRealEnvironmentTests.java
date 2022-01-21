package EverVault;

import EverVault.Exceptions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class WhenUsingApiAgainstRealEnvironmentTests {
    private static final String API_ADDRESS = "api.evervault.io";
    private static final String RUN_ADDRESS = "run.evervault.io";
    private static final String ENV_API_KEY = "ENVIRONMENT_APIKEY";

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
    void doesNotThrowWhenCreatingNewInstanceWithValidKey() throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException {
        new EverVault(getEnvironmentApiKey(), API_ADDRESS, RUN_ADDRESS);
    }

    @Test
    void encryptSomeDataCorrectly() throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException, InvalidCipherException, MandatoryParameterException {
        final String someDataToEncrypt = "Foo";

        var everVault = new EverVault(getEnvironmentApiKey(), API_ADDRESS, RUN_ADDRESS);

        var result = (String)everVault.encrypt(someDataToEncrypt);

        assert !result.isEmpty();
        assert !result.isBlank();
    }
}