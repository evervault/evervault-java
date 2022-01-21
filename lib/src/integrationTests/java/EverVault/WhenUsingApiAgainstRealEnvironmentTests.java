package EverVault;

import EverVault.Exceptions.HttpFailureException;
import EverVault.Exceptions.MaxRetryReachedException;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
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

    @Test
    void weHaveEnvironmentSetupProperly() {
        var envContent = System.getenv(ENV_API_KEY);

        assert !envContent.isEmpty();
        assert !envContent.isBlank();
    }

    @Test
    void doesNotThrowWhenCreatingNewInstanceWithValidKey() throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException {
        //new EverVault("SomeKey", API_ADDRESS, RUN_ADDRESS);
    }
}