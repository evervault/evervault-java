package com.evervault;

import com.evervault.exceptions.*;
import com.evervault.utils.EcdhCurve;
import com.evervault.utils.ProxySystemSettings;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WhenUsingApiAgainstRealEnvironmentTests {
    private static final String ENV_API_KEY = "ENVIRONMENT_API_KEY";
    private static final String ENV_APP_UUID = "ENVIRONMENT_APP_UUID";
    private static final String DEFAULT_CAGE_NAME = "java-integration-test-cage";
    private static final String EV_CAGE_ENV_KEY = "EV_CAGE_NAME";
    private String cageName;

    public WhenUsingApiAgainstRealEnvironmentTests() {
        cageName = System.getenv(EV_CAGE_ENV_KEY);

        if ( cageName == null) {
            cageName  = DEFAULT_CAGE_NAME;
        }
    }

    public SSLContext getSSLContextTrustAny() throws KeyManagementException, NoSuchAlgorithmException {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        return sslContext;
    };

    public String getPayloadWithEncryptedString(Object encryptedString) {
        String msg = "  \"payment\": {\n" +
                "    \"type\": \"visa\",\n" +
                "    \"cardholderName\": \"Claude Shannon\",\n" +
                "    \"cardNumber\": \""+ encryptedString + "\",\n" +
                "    \"expYear\": \"23\"\n" +
                "  },";
        return msg;
    };

    public String getEnvironmentAppUuid() {
        return "app_12345678987";
    }

    public String getEnvironmentApiKey() {
        return System.getenv(ENV_API_KEY);
    }

    @Test
    void weHaveEnvironmentSetupProperly() {
        var envContent = getEnvironmentApiKey();

        assert !envContent.isEmpty();
        assert !envContent.isBlank();
    }

    @Test
    void doesThrowWhenInvalidKey() {
        assertThrows(EvervaultException.class, () -> new Evervault("foo", "bar"));
    }

    @Test
    void encryptSomeDataCorrectly() throws EvervaultException {
        final String someDataToEncrypt = "Foo";
        var evervault = new Evervault("app_id", getEnvironmentApiKey());

        var result = (String) evervault.encrypt(someDataToEncrypt);

        assert !result.isEmpty();
        assert !result.isBlank();

        var split = result.split(":");
        assertEquals(6, split.length);
    }

    private static class Bar {
        public String name;

        public static final String NAME_CONTENT = "Foo";

        public static Bar createFooStructure(Evervault evervault) throws EvervaultException {
            var bar = new Bar();
            bar.name = (String) evervault.encrypt(NAME_CONTENT);

            return bar;
        }
    }

    @Test
    void encryptAndRun() throws EvervaultException {
        var evervault = new Evervault( "app_id", getEnvironmentApiKey());
        var data = Bar.createFooStructure(evervault);
        var cageResult = evervault.run(cageName, data, false, null);

        assert !cageResult.runId.isEmpty();
    }

    @Test
    void encryptAndRunR1Curve() throws EvervaultException {
        var evervault = new Evervault("app_id", getEnvironmentApiKey(), EcdhCurve.SECP256R1);
        var data = Bar.createFooStructure(evervault);
        var cageResult = evervault.run(cageName, data, false, null);

        assert !cageResult.runId.isEmpty();
    }

    @Test
    void createRunToken() throws EvervaultException {
        var evervault = new Evervault("app_id", getEnvironmentApiKey(),);
        var data = Bar.createFooStructure(evervault);
        var runTokenResult = evervault.createRunToken(cageName, data);
        
        assert !runTokenResult.token.isEmpty();
    }

    private static class OwnEvervault extends Evervault {
        public OwnEvervault(String appUuid, String apiKey) throws EvervaultException {
            super(appUuid, apiKey);
        }

        public byte[] getSharedKey() {
            return this.sharedKey;
        }
    }

    private static final int ENCRYPTED_DATA_SPLIT_POSITION = 4;
    private static final int IV_POS = 2;

    @Test
    void decryptDataWorksAsExpected() throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException, InvalidCipherTextException, NotImplementedException, EvervaultException {
        var evervault = new OwnEvervault(getEnvironmentAppUuid(), getEnvironmentApiKey());

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

    @Test
    void interceptWorksThroughApacheHttpLibrary() throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException, InvalidCipherTextException, NotImplementedException, EvervaultException, KeyManagementException {

        var evervault = new Evervault(getEnvironmentAppUuid(), getEnvironmentApiKey(), EcdhCurve.SECP256R1);

        var encryptedString = evervault.encrypt("Secret info");

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(60 * 1000)
                .setConnectionRequestTimeout(60 * 1000)
                .setSocketTimeout(60 * 1000).build();

        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setSSLContext(getSSLContextTrustAny())
                .setDefaultRequestConfig(config)
                .setProxy(ProxySystemSettings.PROXY_HOST)
                .setDefaultCredentialsProvider(evervault.getEvervaultProxyCredentials())
                .build();

        String uri = "https://enssc1aqsjv0g.x.pipedream.net/apache-client";
        String msg = getPayloadWithEncryptedString(encryptedString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofString(msg))
                .build();

        org.apache.http.client.methods.HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(msg));
        CloseableHttpResponse response = httpClient.execute(httpPost);

        httpClient.close();
        Header[] headers = response.getHeaders("x-evervault-ctx");
        assert headers.length > 0;
    }

    @Test
    void interceptWithOutboundRelayConfigWorksThroughApacheHttpLibrary() throws HttpFailureException, NotPossibleToHandleDataTypeException, InvalidAlgorithmParameterException, MaxRetryReachedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, InterruptedException, InvalidCipherTextException, NotImplementedException, EvervaultException, KeyManagementException {
        var evervault = new Evervault(getEnvironmentAppUuid(), getEnvironmentApiKey(), true, EcdhCurve.SECP256R1);

        var encryptedString = evervault.encrypt("Secret info");

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(60 * 1000)
                .setConnectionRequestTimeout(60 * 1000)
                .setSocketTimeout(60 * 1000).build();

        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setSSLContext(getSSLContextTrustAny())
                .setDefaultRequestConfig(config)
                .setProxy(ProxySystemSettings.PROXY_HOST)
                .setDefaultCredentialsProvider(evervault.getEvervaultProxyCredentials())
                .build();

        String uri = "https://enssc1aqsjv0g.x.pipedream.net/apache-client";
        String msg = getPayloadWithEncryptedString(encryptedString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofString(msg))
                .build();

        org.apache.http.client.methods.HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(msg));
        CloseableHttpResponse response = httpClient.execute(httpPost);

        httpClient.close();
        Header[] headers = response.getHeaders("x-evervault-ctx");
        assert headers.length > 0;
    }
}