package com.evervault;

import com.evervault.exceptions.EvervaultException;
import com.evervault.exceptions.MandatoryParameterException;
import com.evervault.models.EvervaultKey;
import com.evervault.services.*;
import com.evervault.utils.EcdhCurve;

import java.util.Objects;

public class Evervault extends EvervaultService {
    private static final String EVERVAULT_API_HOST = "api.evervault.com";
    private static final String EVERVAULT_RUN_HOST = "run.evervault.com";
    private static final String EVERVAULT_RELAY_HOST = "strict.relay.evervault.com";

    private String evervaultApiHost;
    private String evervaultRunHost;
    private String evervaultRelayHost;
    private String[] evervaultIgnoreDomains;
    private String[] evervaultDecryptionDomains;

    public String getEvervaultApiHost() { return evervaultApiHost; }

    public String getEvervaultApiUrl() { return "https://" + evervaultApiHost; }

    public String getEvervaultRunHost() { return evervaultRunHost; };

    public String getEvervaultRunUrl() { return "https://" + evervaultRunHost; }

    public String getEvervaultRelayHost() {
        return evervaultRelayHost;
    }

    public String[] getEvervaultIgnoreDomains() {
        return evervaultIgnoreDomains;
    }

    public String[] getEvervaultDecryptionDomains() {
        return evervaultDecryptionDomains;
    }

    private void setEvervaultApiHost() {
        String envApiHost = System.getenv("EV_API_HOST");
        this.evervaultApiHost = envApiHost != null ? envApiHost : EVERVAULT_API_HOST;
    }

    private void setEvervaultRunHost() {
        String envRunHost = System.getenv("EV_CAGE_RUN_HOST");
        this.evervaultRunHost = envRunHost != null ? envRunHost : EVERVAULT_RUN_HOST;
    }

    private void setEvervaultRelayUrl() {
        String envRelayHost = System.getenv("EV_RELAY_HOST");
        this.evervaultRelayHost = envRelayHost != null ? envRelayHost : EVERVAULT_RELAY_HOST;
    }

    private void setEvervaultIgnoreDomains() {
        this.evervaultIgnoreDomains = new String[]{ getEvervaultApiHost(), getEvervaultRunHost() };
    }

    public Evervault(String appId, String apiKey) throws EvervaultException {
        this(apiKey, appId, EcdhCurve.SECP256K1, null, false, null);
    }

    public Evervault(String appId, String apiKey, EcdhCurve ecdhCurve) throws EvervaultException {
        this(apiKey, appId, ecdhCurve, null, false, null);
    }

    public Evervault(String appId, String apiKey, Boolean enableOutboundRelay, EcdhCurve ecdhCurve) throws EvervaultException {
        this(apiKey, appId, ecdhCurve, null, enableOutboundRelay, null);
    }

    public Evervault(String appId, String apiKey, Boolean enableOutboundRelay) throws EvervaultException {
        this(apiKey, appId, EcdhCurve.SECP256K1, null, enableOutboundRelay, null);
    }

    /**
     * Builds a client that encrypts with the given key, without calling the Evervault API.
     * The client cannot decrypt, run Functions, or use Outbound Relay.
     */
    public static Evervault withKey(String appId, EvervaultKey key) throws EvervaultException {
        return withKey(appId, null, key);
    }

    /**
     * Builds a client that encrypts with the given key rather than one fetched from the
     * Evervault API. The API key is used for operations that need the API, such as decryption.
     */
    public static Evervault withKey(String appId, String apiKey, EvervaultKey key) throws EvervaultException {
        return new Evervault(apiKey, appId, requireKey(key).getCurve(), null, false, key);
    }

    private static EvervaultKey requireKey(EvervaultKey key) throws EvervaultException {
        if (key == null) {
            throw new EvervaultException(new MandatoryParameterException("key"));
        }

        return key;
    }

    private Evervault(String apiKey, String appUuid, EcdhCurve ecdhCurve, String[] decryptionDomains, Boolean enableOutboundRelay, EvervaultKey key) throws EvervaultException {
        setEvervaultApiHost();
        setEvervaultRunHost();
        setEvervaultRelayUrl();
        setEvervaultIgnoreDomains();

        this.evervaultDecryptionDomains = decryptionDomains;

        HttpHandler httpHandler = new HttpHandler(apiKey, appUuid);
        EncryptionService encryptService = EncryptionServiceFactory.build(ecdhCurve);
        CircuitBreaker circuitBreaker = new CircuitBreaker();
        TimeService timeService = new TimeService();
        RepeatableTaskSchedulerService taskScheduler = new RepeatableTaskSchedulerService();

        this.setupCircuitBreaker(circuitBreaker);
        this.setupCageExecutionProvider(httpHandler);
        this.setupRunTokenProvider(httpHandler);
        this.setupOutboundRelayConfigProvider(httpHandler);
        this.setupDecryptProvider(httpHandler);
        this.setupRepeatableTaskScheduler(taskScheduler);
        this.setupClientSideTokenProvider(httpHandler);
        this.setupFunctionRunProvider(httpHandler);

        if (key != null) {
            this.setupKeyProviders(encryptService, encryptService, timeService, key);
        } else {
            this.setupKeyProviders(httpHandler, encryptService, encryptService, timeService, ecdhCurve);
        }

        EvervaultEncryptionService encryptForObject = new EvervaultEncryptionService(encryptService, this.generatedEcdhKey, this.sharedKey, this.teamKey);

        this.setupEncryption(encryptForObject);

        if (key == null) {
            this.setupCredentialsProvider(apiKey);
        }

        if (key == null && (decryptionDomains != null || enableOutboundRelay != null && enableOutboundRelay)) {
            this.setupIntercept(decryptionDomains, evervaultIgnoreDomains);
        }
    }
}
