package evervault;

import evervault.Exceptions.HttpFailureException;
import evervault.Exceptions.MaxRetryReachedException;
import evervault.Exceptions.NotPossibleToHandleDataTypeException;
import evervault.Services.*;
import evervault.utils.EcdhCurve;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

public class Evervault extends EvervaultService {
    private static final String EVERVAULT_BASE_URL = "https://api.evervault.com/";
    private static final String EVERVAULT_RUN_URL = "https://run.evervault.com/";
    private String evervaultApiUrl;
    private String evervaultRunUrl;

    public String getEvervaultBaseUrl() {
        return evervaultApiUrl;
    }

    public String getEvervaultRunUrl() {
        return evervaultRunUrl;
    }

    private void setEvervaultBaseUrl() {
        var envApiUrl = System.getenv("EV_API_URL");
        this.evervaultApiUrl = Objects.requireNonNullElse(envApiUrl, EVERVAULT_BASE_URL);
    }

    private void setEvervaultRunUrl() {
        var envRunUrl = System.getenv("EV_CAGE_RUN_URL");
        this.evervaultRunUrl = Objects.requireNonNullElse(envRunUrl, EVERVAULT_RUN_URL);
    }

    public Evervault(String apiKey) throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException {
        this(apiKey, EcdhCurve.SECP256K1);
    }

    public Evervault(String apiKey, EcdhCurve ecdhCurve) throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException {
        setEvervaultBaseUrl();
        setEvervaultRunUrl();

        var httpHandler = new HttpHandler(apiKey);
        var encryptService = EncryptionServiceFactory.build(ecdhCurve);
        var circuitBreaker = new CircuitBreaker();
        var timeService = new TimeService();

        this.setupCircuitBreaker(circuitBreaker);
        this.setupCageExecutionProvider(httpHandler);

        this.setupKeyProviders(httpHandler, encryptService, encryptService, timeService, ecdhCurve);

        var encryptForObject = new EvervaultEncryptionService(encryptService, this.generatedEcdhKey, this.sharedKey);

        this.setupEncryption(encryptForObject);
    }
}
