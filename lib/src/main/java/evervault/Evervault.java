package evervault;

import evervault.Exceptions.HttpFailureException;
import evervault.Exceptions.MaxRetryReachedException;
import evervault.Exceptions.NotPossibleToHandleDataTypeException;
import evervault.Services.*;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class Evervault extends EvervaultService {
    private static final String EVERVAULT_BASE_URL = "https://api.evervault.com/";
    private static final String EVERVAULT_RUN_URL = "https://run.evervault.com/";
    private final String evervaultApiUrl;
    private final String evervaultRunUrl;

    public String getEvervaultBaseUrl() {
        return evervaultApiUrl;
    }

    public String getEvervaultRunUrl() {
        return evervaultRunUrl;
    }

    public Evervault(String apiKey, String evervaultApiUrl, String evervaultRunUrl) throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException {
        this.evervaultApiUrl = evervaultApiUrl;
        this.evervaultRunUrl = evervaultRunUrl;

        var httpHandler = new HttpHandler(apiKey);
        var encryptService = new EncryptionService(new StdEncryptionOutputFormat());
        var circuitBreaker = new CircuitBreaker();
        var timeService = new TimeService();

        this.setupCircuitBreaker(circuitBreaker);
        this.setupCageExecutionProvider(httpHandler);

        this.setupKeyProviders(httpHandler, encryptService, encryptService, timeService);

        var encryptForObject = new EvervaultEncryptionService(encryptService, this.generatedEcdhKey, this.sharedKey);

        this.setupEncryption(encryptForObject);
    }

    public Evervault(String apiKey) throws HttpFailureException, InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InterruptedException, NotPossibleToHandleDataTypeException, MaxRetryReachedException, NoSuchProviderException {
        this(apiKey, EVERVAULT_BASE_URL, EVERVAULT_RUN_URL);
    }
}
