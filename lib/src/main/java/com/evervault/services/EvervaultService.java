/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.evervault.services;

import com.evervault.contracts.*;
import com.evervault.exceptions.*;
import com.evervault.models.CageRunResult;
import com.evervault.models.RunTokenResult;
import com.evervault.utils.EcdhCurve;
import com.evervault.utils.ProxyCredentialsProvider;
import com.evervault.utils.ProxyRoutePlanner;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.routing.HttpRoutePlanner;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.Instant;

public abstract class EvervaultService {
    protected IProvideCagePublicKeyFromHttpApi cagePublicKeyFromEndpointProvider;
    protected IProvideECPublicKey ecPublicKeyProvider;
    protected IProvideSharedKey sharedKeyProvider;
    protected IProvideEncryptionForObject encryptionProvider;
    protected IProvideCageExecution cageExecutionProvider;
    protected IProvideRunToken runTokenProvider;
    protected IProvideCircuitBreaker circuitBreakerProvider;
    protected CredentialsProvider credentialsProvider;

    protected HttpRoutePlanner httpRoutePlanner;

    protected final static int NEW_KEY_TIMESTAMP = 15;
    protected final static String RELAY_PORT = "443";
    protected final int getCageHash = "getCagePublicKeyFromEndpoint".hashCode();
    protected final int runCageHash = "runCage".hashCode();
    protected final int createRunTokenHash = "createRunToken".hashCode();
    protected Instant currentSharedKeyTimestamp;
    protected byte[] generatedEcdhKey;
    protected byte[] sharedKey;
    protected IProvideTime timeProvider;
    protected PublicKey teamKey;
    protected String teamUuid;
    protected Boolean intercept = true;

    // Virtual method
    protected String getEvervaultApiHost() { return ""; }

    // Virtual method
    protected String getEvervaultApiUrl() { return ""; }

    // Virtual method
    protected String getEvervaultRunHost() {
        return "";
    }

    // Virtual method
    protected String getEvervaultRunUrl() {
        return "";
    }

    // Virtual method
    protected String getEvervaultRelayHost() { return ""; }

    // Virtual method
    protected String[] getEvervaultIgnoreDomains() { return new String[0]; }

    protected String[] getEvervaultDecryptionDomains() { return new String[0]; }

    protected void setupCircuitBreaker(IProvideCircuitBreaker provideCircuitBreaker) {
        if (provideCircuitBreaker == null) {
            throw new NullPointerException(IProvideCircuitBreaker.class.getName());
        }

        this.circuitBreakerProvider = provideCircuitBreaker;
    }

    protected void setupCageExecutionProvider(IProvideCageExecution cageExecutionProvider) {
        if (cageExecutionProvider == null) {
            throw new NullPointerException(IProvideCageExecution.class.getName());
        }

        this.cageExecutionProvider = cageExecutionProvider;
    }

    protected void setupRunTokenProvider(IProvideRunToken runTokenProvider) {
        if (runTokenProvider == null) {
            throw new NullPointerException(IProvideRunToken.class.getName());
        }

        this.runTokenProvider = runTokenProvider;
    }

    protected void setupKeyProviders(IProvideCagePublicKeyFromHttpApi cagePublicKeyFromEndpointProvider,
                                     IProvideECPublicKey ecPublicKeyProvider,
                                     IProvideSharedKey sharedKeyProvider,
                                     IProvideTime timeProvider,
                                     EcdhCurve ecdhCurve) throws EvervaultException {
        if (cagePublicKeyFromEndpointProvider == null) {
            throw new NullPointerException(IProvideCagePublicKeyFromHttpApi.class.getName());
        }

        if (ecPublicKeyProvider == null) {
            throw new NullPointerException(IProvideECPublicKey.class.getName());
        }

        if (sharedKeyProvider == null) {
            throw new NullPointerException(IProvideSharedKey.class.getName());
        }

        if (timeProvider == null) {
            throw new NullPointerException(IProvideTime.class.getName());
        }

        this.timeProvider = timeProvider;
        this.cagePublicKeyFromEndpointProvider = cagePublicKeyFromEndpointProvider;
        this.ecPublicKeyProvider = ecPublicKeyProvider;
        this.sharedKeyProvider = sharedKeyProvider;

        try {
            setupKeys(ecdhCurve);
        } catch (HttpFailureException | InterruptedException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidAlgorithmParameterException | InvalidKeyException | NotPossibleToHandleDataTypeException | MaxRetryReachedException | NoSuchProviderException | NotImplementedException | IOException | Asn1EncodingException e) {
            throw new EvervaultException(e);
        }
    }

    protected void setupEncryption(IProvideEncryptionForObject encryptionProvider) {
        if (encryptionProvider == null) {
            throw new NullPointerException(IProvideSharedKey.class.getName());
        }

        this.encryptionProvider = encryptionProvider;
    }

    private void setupKeys(EcdhCurve ecdhCurve) throws HttpFailureException, IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, NotPossibleToHandleDataTypeException, MaxRetryReachedException, NoSuchProviderException, NotImplementedException, Asn1EncodingException {
        var teamEcdhKey = circuitBreakerProvider.execute(getCageHash, () -> cagePublicKeyFromEndpointProvider.getCagePublicKeyFromEndpoint(getEvervaultApiUrl()));

        teamUuid = teamEcdhKey.teamUuid;

        if (ecdhCurve.equals(EcdhCurve.SECP256R1)) {
            teamKey = ecPublicKeyProvider.getEllipticCurvePublicKeyFrom(teamEcdhKey.ecdhP256Key);
        } else {
            teamKey = ecPublicKeyProvider.getEllipticCurvePublicKeyFrom(teamEcdhKey.ecdhKey);
        }

        generateSharedKey();
    }

    protected void setupIntercept(String apiKey) {

        String user = teamUuid;
        String password = apiKey;
        String proxyHost = getEvervaultRelayHost();
        String proxyPort = RELAY_PORT;

        String[] evervaultIgnoreDomains = getEvervaultIgnoreDomains();
        this.setupHttpRoutePlanner(evervaultIgnoreDomains);
        String ignoreDomains = String.join("|", evervaultIgnoreDomains);

        System.setProperty("jdk.https.auth.tunneling.disabledSchemes", "");
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
        System.setProperty("https.proxyUser", user);
        System.setProperty("https.proxyPassword", password);
        System.setProperty("https.nonProxyHosts", ignoreDomains);
        
        System.setProperty("http.proxyUser", user);
        System.setProperty("http.proxyPassword", password);
        System.setProperty("http.nonProxyHosts", ignoreDomains);
    }

    protected void setupInterceptV2() {
        String[] decryptionDomains = getEvervaultDecryptionDomains();
        this.setupHttpRoutePlannerV2(decryptionDomains);
    }

    //Used for Apache Http Clients
    protected void setupCredentialsProvider(String apiKey) {
        this.credentialsProvider = ProxyCredentialsProvider
                .getEvervaultCredentialsProvider(getEvervaultRelayHost(), Integer.valueOf(RELAY_PORT), teamUuid, apiKey);
    }

    //Returns a CredentialsProvider to authenticate an
    // Apache HttpClient with the Evervault Proxy.
    public CredentialsProvider getEvervaultProxyCredentials() {
        return this.credentialsProvider;
    }

    protected void setupHttpRoutePlanner(String[] ignoreDomains) {
        this.httpRoutePlanner = ProxyRoutePlanner
                .getEvervaultRoutePlanner(ignoreDomains);
    }

    protected void setupHttpRoutePlannerV2(String[] decryptionDomains) {
        this.httpRoutePlanner = ProxyRoutePlanner
                .getEvervaultRoutePlannerV2(decryptionDomains, getEvervaultIgnoreDomains());
    }

    public HttpRoutePlanner getEvervaultHttpRoutePlanner() { return this.httpRoutePlanner; }

    private void generateSharedKey() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NotImplementedException, Asn1EncodingException {
        currentSharedKeyTimestamp = timeProvider.GetNow();
        var generated = sharedKeyProvider.generateSharedKeyBasedOn(teamKey);

        this.sharedKey = generated.SharedKey;
        this.generatedEcdhKey = generated.GeneratedEcdhKey;
    }

    public Object encrypt(Object data) throws EvervaultException {
        if (data == null) {
            throw new EvervaultException(new MandatoryParameterException("data"));
        }

        updateKeysIfTimeIsDue();

        try {
            return this.encryptionProvider.encrypt(data);
        } catch (NotPossibleToHandleDataTypeException | IOException | InvalidCipherException | NotImplementedException e) {
            throw new EvervaultException(e);
        }
    }

    protected void updateKeysIfTimeIsDue() throws EvervaultException {
        if (Duration.between(currentSharedKeyTimestamp, timeProvider.GetNow()).toMinutes() >= NEW_KEY_TIMESTAMP) {
            try {
                generateSharedKey();
            } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | NotImplementedException | Asn1EncodingException e) {
                throw new EvervaultException(e);
            }
        }
    }

    public CageRunResult run(String cageName, Object data, boolean async, String version) throws EvervaultException {
        if (cageName == null || cageName.isEmpty()) {
            throw new EvervaultException(new MandatoryParameterException("cageName"));
        }

        if (data == null) {
            throw new EvervaultException(new MandatoryParameterException("data"));
        }

        try {
            return circuitBreakerProvider.execute(runCageHash, () -> cageExecutionProvider.runCage(getEvervaultRunUrl(), cageName, data, async, version));
        } catch (MaxRetryReachedException | HttpFailureException | NotPossibleToHandleDataTypeException | IOException | InterruptedException e) {
            throw new EvervaultException(e);
        }
    }

    public RunTokenResult createRunToken(String cageName, Object data) throws EvervaultException {
        if (cageName == null || cageName.isEmpty()) {
            throw new EvervaultException(new MandatoryParameterException("cageName"));
        }

        if (data == null) {
            throw new EvervaultException(new MandatoryParameterException("data"));
        }

        try {
            return circuitBreakerProvider.execute(createRunTokenHash, () -> runTokenProvider.createRunToken(getEvervaultApiUrl(), cageName, data));
        } catch (MaxRetryReachedException | HttpFailureException | NotPossibleToHandleDataTypeException | IOException | InterruptedException e) {
            throw new EvervaultException(e);
        }
    }
}
