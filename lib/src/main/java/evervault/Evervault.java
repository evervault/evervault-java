package evervault;

import evervault.exceptions.EvervaultException;
import evervault.services.*;
import evervault.utils.EcdhCurve;

import java.util.Objects;

public class Evervault extends EvervaultService {
    private static final String EVERVAULT_BASE_URL = "https://api.evervault.com/";
    private static final String EVERVAULT_RUN_URL = "https://run.evervault.com/";
    private static final String EVERVAULT_BASE_HOST = "api.evervault.com";
    private static final String EVERVAULT_RUN_HOST = "run.evervault.com";
    private static final String EVERVAULT_RELAY_HOST = "strict.relay.evervault.com";

    private String evervaultApiUrl;
    private String evervaultRunUrl;
    private String evervaultRelayUrl;
    private String[] evervaultIgnoreDomains;

    public String getEvervaultBaseUrl() {
        return evervaultApiUrl;
    }

    public String getEvervaultRunUrl() {
        return evervaultRunUrl;
    }

    public String getEvervaultRelayUrl() {
        return evervaultRelayUrl;
    }

    public String[] getEvervaultIgnoreDomains() {
        return evervaultIgnoreDomains;
    }

    private void setEvervaultBaseUrl() {
        var envApiUrl = System.getenv("EV_API_URL");
        this.evervaultApiUrl = Objects.requireNonNullElse(envApiUrl, EVERVAULT_BASE_URL);
    }

    private void setEvervaultRunUrl() {
        var envRunUrl = System.getenv("EV_CAGE_RUN_URL");
        this.evervaultRunUrl = Objects.requireNonNullElse(envRunUrl, EVERVAULT_RUN_URL);
    }

    private void setEvervaultRelayUrl() {
        var envRelayUrl = System.getenv("EV_RELAY_HOST");
        this.evervaultRelayUrl = Objects.requireNonNullElse(envRelayUrl, EVERVAULT_RELAY_HOST);
    }

    private void setEvervaultIgnoreDomains(String[] ignoreDomains) {
        String[] defaultDomains = {EVERVAULT_BASE_HOST, EVERVAULT_RUN_HOST};
        if (ignoreDomains == null) {
            this.evervaultIgnoreDomains = defaultDomains;
        } else {
            this.evervaultIgnoreDomains = mergeIgnoreDomains(defaultDomains, ignoreDomains);
        }
    }

    private String[] mergeIgnoreDomains(String[] defaultDomains, String[] ignoreDomains) {
        var defaultLength = defaultDomains.length;
        var ignoreDomainsLength = ignoreDomains.length;

        var mergedLength = defaultLength + ignoreDomainsLength;
        String[] mergedDomains = new String[mergedLength];

        System.arraycopy(defaultDomains, 0, mergedDomains, 0, defaultLength);
        System.arraycopy(ignoreDomains, 0, mergedDomains, defaultLength, ignoreDomainsLength);

        return mergedDomains;
    }

    public Evervault(String apiKey) throws EvervaultException {
        this(apiKey, EcdhCurve.SECP256K1, true, null);
    }

    public Evervault(String apiKey, Boolean intercept) throws EvervaultException {
        this(apiKey, EcdhCurve.SECP256K1, intercept, null);
    }

    public Evervault(String apiKey, Boolean intercept, String[] ignoreDomains) throws EvervaultException {
        this(apiKey, EcdhCurve.SECP256K1, intercept, ignoreDomains);
    }

    public Evervault(String apiKey, EcdhCurve ecdhCurve) throws EvervaultException {
        this(apiKey, ecdhCurve, true, null);
    }

    public Evervault(String apiKey, EcdhCurve ecdhCurve, Boolean intercept) throws EvervaultException {
        this(apiKey, ecdhCurve, intercept, null);
    }

    public Evervault(String apiKey, EcdhCurve ecdhCurve, String[] ignoreDomains) throws EvervaultException {
        this(apiKey, ecdhCurve, true, ignoreDomains);
    }

    public Evervault(String apiKey, EcdhCurve ecdhCurve, Boolean intercept, String[] ignoreDomains) throws EvervaultException {
        setEvervaultBaseUrl();
        setEvervaultRunUrl();
        setEvervaultRelayUrl();
        setEvervaultIgnoreDomains(ignoreDomains);

        var httpHandler = new HttpHandler(apiKey);
        var encryptService = EncryptionServiceFactory.build(ecdhCurve);
        var circuitBreaker = new CircuitBreaker();
        var timeService = new TimeService();

        this.setupCircuitBreaker(circuitBreaker);
        this.setupCageExecutionProvider(httpHandler);

        this.setupKeyProviders(httpHandler, encryptService, encryptService, timeService, ecdhCurve);

        var encryptForObject = new EvervaultEncryptionService(encryptService, this.generatedEcdhKey, this.sharedKey);

        this.setupEncryption(encryptForObject);


        if (intercept) {
            System.out.println("SETTING UP INTERCEPT");
            this.setupIntercept(apiKey);

        }
    }
}
