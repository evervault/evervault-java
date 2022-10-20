package com.evervault;

import com.evervault.exceptions.EvervaultException;
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

    private boolean enableOutboundRelay;

    public String getEvervaultApiHost() { return evervaultApiHost; }

    public String getEvervaultApiUrl() { return "https://" + evervaultApiHost + "/"; }

    public String getEvervaultRunHost() { return evervaultRunHost; };

    public String getEvervaultRunUrl() { return "https://" + evervaultRunHost + "/"; }

    public String getEvervaultRelayHost() {
        return evervaultRelayHost;
    }

    public String[] getEvervaultIgnoreDomains() {
        return evervaultIgnoreDomains;
    }
    public String[] getEvervaultDecryptionDomains() {
        return evervaultDecryptionDomains;
    }

    public boolean isEnableOutboundRelay() {
        return enableOutboundRelay;
    }

    private void setEvervaultApiHost() {
        var envApiHost = System.getenv("EV_API_HOST");
        this.evervaultApiHost = Objects.requireNonNullElse(envApiHost, EVERVAULT_API_HOST);
    }

    private void setEvervaultRunHost() {
        var envRunHost = System.getenv("EV_CAGE_RUN_HOST");
        this.evervaultRunHost = Objects.requireNonNullElse(envRunHost, EVERVAULT_RUN_HOST);
    }

    private void setEvervaultRelayUrl() {
        var envRelayHost = System.getenv("EV_RELAY_HOST");
        this.evervaultRelayHost = Objects.requireNonNullElse(envRelayHost, EVERVAULT_RELAY_HOST);
    }

    private void setEvervaultIgnoreDomains(String[] ignoreDomains) {
        String[] defaultDomains = {getEvervaultApiHost(), getEvervaultRunHost()};
        if (ignoreDomains == null) {
            this.evervaultIgnoreDomains = defaultDomains;
        } else {
            this.evervaultIgnoreDomains = mergeIgnoreDomains(defaultDomains, ignoreDomains);
        }
    }

    public void setEnableOutboundRelay(boolean enableOutboundRelay) {
        this.enableOutboundRelay = enableOutboundRelay;
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
        this(apiKey, null, EcdhCurve.SECP256K1);
    }

    public Evervault(String apiKey, Boolean intercept) throws EvervaultException {
        this(apiKey, EcdhCurve.SECP256K1, intercept, null, null, false);
    }

    public Evervault(String apiKey, Boolean intercept, Boolean enableOutboundRelay) throws EvervaultException {
        this(apiKey, EcdhCurve.SECP256K1, intercept, null, null, enableOutboundRelay);
    }

    public Evervault(String apiKey, String[] decryptionDomains) throws EvervaultException {
        this(apiKey, decryptionDomains, EcdhCurve.SECP256K1);
    }

    public Evervault(String apiKey, Boolean intercept, String[] ignoreDomains) throws EvervaultException {
        this(apiKey, EcdhCurve.SECP256K1, intercept, ignoreDomains, null, false);
    }

    public Evervault(String apiKey, EcdhCurve ecdhCurve) throws EvervaultException {
        this(apiKey, null, ecdhCurve);
    }

    public Evervault(String apiKey, EcdhCurve ecdhCurve, Boolean intercept) throws EvervaultException {
        this(apiKey, ecdhCurve, intercept, null, null, false);
    }

    public Evervault(String apiKey, EcdhCurve ecdhCurve, String[] ignoreDomains) throws EvervaultException {
        this(apiKey, ecdhCurve, true, ignoreDomains, null, false);
    }

    public Evervault(String apiKey, EcdhCurve ecdhCurve, Boolean intercept, String[] ignoreDomains) throws EvervaultException {
        this(apiKey, ecdhCurve, intercept, ignoreDomains, null, false);
        System.out.println(
                "The `intercept` and `ignoreDomains` config options in Evervault Node.js SDK are deprecated and slated for removal." +
                "\nPlease switch to the `decryptionDomains` config option." +
                "\nMore details: https://docs.evervault.com/reference/nodejs-sdk#evervaultsdk"
        );
    }

    public Evervault(String apiKey, String[] decryptionDomains, EcdhCurve ecdhCurve) throws EvervaultException {
        this(apiKey, ecdhCurve, false, null, decryptionDomains, false);
    }

    private Evervault(String apiKey, EcdhCurve ecdhCurve, Boolean intercept, String[] ignoreDomains, String[] decryptionDomains, boolean enableOutboundRelay) throws EvervaultException {
        setEvervaultApiHost();
        setEvervaultRunHost();
        setEvervaultRelayUrl();
        setEvervaultIgnoreDomains(ignoreDomains);
        this.evervaultDecryptionDomains = decryptionDomains;
        this.setEnableOutboundRelay(enableOutboundRelay);

        var httpHandler = new HttpHandler(apiKey);
        var encryptService = EncryptionServiceFactory.build(ecdhCurve);
        var circuitBreaker = new CircuitBreaker();
        var timeService = new TimeService();

        this.setupCircuitBreaker(circuitBreaker);
        this.setupCageExecutionProvider(httpHandler);
        this.setupRunTokenProvider(httpHandler);
        this.setupRelayOutboundConfigProvider(httpHandler);

        this.setupKeyProviders(httpHandler, encryptService, encryptService, timeService, ecdhCurve);
        var encryptForObject = new EvervaultEncryptionService(encryptService, this.generatedEcdhKey, this.sharedKey, this.teamKey);

        this.setupEncryption(encryptForObject);
        this.setupCredentialsProvider(apiKey);

        if (enableOutboundRelay) {
            this.setupOutboundRelay();
        }

        if (intercept) { this.setupIntercept(apiKey); }

        if (decryptionDomains != null && decryptionDomains.length > 0) {
            this.setupInterceptV2();
        }
    }
}
