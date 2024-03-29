package com.evervault;

import com.evervault.contracts.IProvideOutboundRelayConfigFromHttpApi;
import com.evervault.exceptions.HttpFailureException;
import com.evervault.services.CachedOutboundRelayConfigService;
import com.evervault.services.HttpHandler;
import com.evervault.models.TokenResult;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.internal.LinkedTreeMap;

import org.eclipse.jetty.util.IO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
public class WhenPerformingHttpRequestTests {
    private static final String USER_AGENT_HEADER = "evervault-java/1.0";
    private static final String CONTENT_TYPE = "application/json";
    private static final String API_KEY = "Foo";
    private static final String APP_UUID = "Bar";
    private static final String RAW_TEXT_CAGES_KEY_ENDPOINT = "{\"teamUuid\":\"de7350990fd7\",\"key\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo7+jkmJ1uZsmiA5omE96RaepPYj2J6DzlE0DNWPoVZZNVb/ShqxSA4zKfE9Kh4MuI6fKpg0/pMhf8Re398ac9s2xKsjDvQHOhLLOfmgcrQgZyLGvdsrllcb1JY8kLNTdgONpn3S/BQetdEPG7oFp1RRIw60Iyy+v2R+r092zItbqLUpb0Vpu2z2uMxylZFc33VuDVIFF+fc9vE0gVPFoHezZ+1+EmqiJdkH/1GcPoVswzCvg3djmCo3Zhx3GdiB464GOl2ZlujwSN9dPkFhndIUZYK9iJhlcItyGkKH1OV/HAl8k2u/7pKUDLFe4lMWX9yASuj6y3CLdrPcbAuky3QIDAQAB\",\"ecdhKey\":\"AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr\"}";

    protected class CardData {
        String cardNumber;
        int cvv;
        String expiry; 
    }

    protected class SomeClass {
        String foo;
        String bar;
        String baz;
    }


    private void assertHeadersForCageKey(String endpoint, String apiKey, HashMap<String, String> headerMap) {
        var pattern = getRequestedFor(urlEqualTo(endpoint))
                .withHeader("User-Agent", equalTo(USER_AGENT_HEADER))
                .withHeader("AcceptEncoding", equalTo("gzip, deflate"))
                .withHeader("Accept", equalTo(CONTENT_TYPE))
                .withHeader("Content-Type", equalTo(CONTENT_TYPE))
                .withHeader("Api-Key", equalTo(apiKey));

        for (HashMap.Entry<String, String> item : headerMap.entrySet()) {
            pattern = pattern.withHeader(item.getKey(), equalTo(item.getValue()));
        }

        verify(pattern);
    }

    private void assertHeadersForCageRun(String endpoint, String apiKey, HashMap<String, String> headerMap) {
        var pattern = postRequestedFor(urlEqualTo(endpoint))
                .withHeader("User-Agent", equalTo(USER_AGENT_HEADER))
                .withHeader("Accept", equalTo(CONTENT_TYPE))
                .withHeader("Content-Type", equalTo(CONTENT_TYPE))
                .withHeader("Api-Key", equalTo(apiKey));

        for (HashMap.Entry<String, String> item : headerMap.entrySet()) {
            pattern = pattern.withHeader(item.getKey(), equalTo(item.getValue()));
        }

        verify(pattern);
    }

    @Test
    public void getCageUrlEndingWithSlashDoesNotThrow(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String endpoint = "/cages/key";

        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(RAW_TEXT_CAGES_KEY_ENDPOINT)));

        var client = new HttpHandler(API_KEY, APP_UUID);

        client.getCagePublicKeyFromEndpoint(wireMockRuntimeInfo.getHttpBaseUrl() + "/");
    }

    @Test
    public void runCageUrlEndingWithSlashDoesNotThrow(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String cageNameEndpoint = "/test-cage";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(post(urlEqualTo(cageNameEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"result\":{\"message\":\"someMessage\",\"name\":\"someEncryptedData\"},\"runId\":\"s0m3Str1ngW1thNumb3rs\"}")
                .withStatus(200)));

        var data = new SomeData();
        data.name = "test";

        client.runCage(wireMockRuntimeInfo.getHttpBaseUrl() + "/", "test-cage", data, true, "1.0.0");
    }

    @Test
    public void httpHeadersAreIncludedForGets(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException, InterruptedException, HttpFailureException {
        final String endpoint = "/cages/key";

        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(RAW_TEXT_CAGES_KEY_ENDPOINT)));

        var client = new HttpHandler(API_KEY, APP_UUID);

        client.getCagePublicKeyFromEndpoint(wireMockRuntimeInfo.getHttpBaseUrl());

        assertHeadersForCageKey(endpoint, API_KEY, new HashMap<>());
    }

    @Test
    void additionalHeadersAreIncluded(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException, InterruptedException, HttpFailureException {
        final String endpoint = "/cages/key";

        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(RAW_TEXT_CAGES_KEY_ENDPOINT)));

        var client = new HttpHandler(API_KEY, APP_UUID);

        var headerMap = new HashMap<String, String>();
        headerMap.put("Foo", "Bar");
        headerMap.put("Bar", "Foo");

        client.getCagePublicKeyFromEndpoint(wireMockRuntimeInfo.getHttpBaseUrl(), headerMap);

        assertHeadersForCageKey(endpoint, API_KEY, headerMap);
    }

    @Test
    void hittingCagePublicKeyEndpointParsesItCorrectly(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException, InterruptedException, HttpFailureException {
        var client = new HttpHandler(API_KEY, APP_UUID);
        final var urlPath = "/cages/key";

        stubFor(get(urlEqualTo(urlPath))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(RAW_TEXT_CAGES_KEY_ENDPOINT)));

        var cagesKey = client.getCagePublicKeyFromEndpoint(wireMockRuntimeInfo.getHttpBaseUrl());

        Assertions.assertEquals(cagesKey.key, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo7+jkmJ1uZsmiA5omE96RaepPYj2J6DzlE0DNWPoVZZNVb/ShqxSA4zKfE9Kh4MuI6fKpg0/pMhf8Re398ac9s2xKsjDvQHOhLLOfmgcrQgZyLGvdsrllcb1JY8kLNTdgONpn3S/BQetdEPG7oFp1RRIw60Iyy+v2R+r092zItbqLUpb0Vpu2z2uMxylZFc33VuDVIFF+fc9vE0gVPFoHezZ+1+EmqiJdkH/1GcPoVswzCvg3djmCo3Zhx3GdiB464GOl2ZlujwSN9dPkFhndIUZYK9iJhlcItyGkKH1OV/HAl8k2u/7pKUDLFe4lMWX9yASuj6y3CLdrPcbAuky3QIDAQAB");
        Assertions.assertEquals(cagesKey.ecdhKey, "AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr");
        Assertions.assertEquals(cagesKey.teamUuid, "de7350990fd7");
    }

    @Test
    void httpStatusNotOkMustThrow(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var client = new HttpHandler(API_KEY, APP_UUID);
        final var urlPath = wireMockRuntimeInfo.getHttpBaseUrl() + "/cages/key";

        assertThrows(HttpFailureException.class, () -> client.getCagePublicKeyFromEndpoint(urlPath));
    }

    private static class SomeData implements Serializable {
        public String name;
    }

    @Test
    void hittingCageRunEndpointValidatesBasicHeaders(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String cageNameEndpoint = "/test-cage";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(post(urlEqualTo(cageNameEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"result\":{\"message\":\"someMessage\",\"name\":\"someEncryptedData\"},\"runId\":\"s0m3Str1ngW1thNumb3rs\"}")
                .withStatus(200)));

        var data = new SomeData();
        data.name = "test";

        client.runCage(wireMockRuntimeInfo.getHttpBaseUrl(), "test-cage", data, true, "1.0.0");

        assertHeadersForCageRun(cageNameEndpoint, API_KEY, new HashMap<>());
    }

    @Test
    void hittingCageRunEndpointWorksCorrectly(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String cageName = "/test-cage";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(post(urlEqualTo(cageName)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"result\":{\"message\":\"someMessage\",\"name\":\"someEncryptedData\"},\"runId\":\"s0m3Str1ngW1thNumb3rs\"}")
                .withStatus(200)));

        var data = new SomeData();
        data.name = "test";

        var result = client.runCage(wireMockRuntimeInfo.getHttpBaseUrl(), "test-cage", data, false, "1.0.0");

        Assertions.assertEquals("s0m3Str1ngW1thNumb3rs", result.runId);
        var nested = (LinkedTreeMap<String, String>) result.result;

        assertEquals("someMessage", nested.get("message"));
        assertEquals("someEncryptedData", nested.get("name"));
    }

    @Test
    void hittingCageRunEndpointThrows(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var client = new HttpHandler(API_KEY, APP_UUID);

        var data = new SomeData();
        data.name = "test";

        assertThrows(HttpFailureException.class, () -> client.runCage(wireMockRuntimeInfo.getHttpBaseUrl(), "test-cage", data, false, "1.0.0"));
    }

    @Test
    void validatesAsyncTrueHeaderWhenHittingCageRunEndpoint(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String cageNameEndpoint = "/test-cage";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(post(urlEqualTo(cageNameEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"result\":{\"message\":\"someMessage\",\"name\":\"someEncryptedData\"},\"runId\":\"s0m3Str1ngW1thNumb3rs\"}")
                .withStatus(200)));

        var data = new SomeData();
        data.name = "test";

        client.runCage(wireMockRuntimeInfo.getHttpBaseUrl(), "test-cage", data, true, "1.0.0");

        var pattern = postRequestedFor(urlEqualTo(cageNameEndpoint))
                .withHeader("x-async", equalTo("true"))
                .withHeader("x-version-id", equalTo("1.0.0"));

        verify(pattern);
    }

    @Test
    void validatesAsyncFalseHeaderWhenHittingCageRunEndpoint(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String cageNameEndpoint = "/test-cage";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(post(urlEqualTo(cageNameEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"result\":{\"message\":\"someMessage\",\"name\":\"someEncryptedData\"},\"runId\":\"s0m3Str1ngW1thNumb3rs\"}")
                .withStatus(200)));

        var data = new SomeData();
        data.name = "test";

        client.runCage(wireMockRuntimeInfo.getHttpBaseUrl(), "test-cage", data, false, null);

        var pattern = postRequestedFor(urlEqualTo(cageNameEndpoint))
                .withoutHeader("x-async")
                .withoutHeader("x-version-id");

        verify(pattern);
    }

    @Test
    void asyncHeaderIsIgnoredWhenVersionIsEmpty(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String cageNameEndpoint = "/test-cage";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(post(urlEqualTo(cageNameEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"result\":{\"message\":\"someMessage\",\"name\":\"someEncryptedData\"},\"runId\":\"s0m3Str1ngW1thNumb3rs\"}")
                .withStatus(200)));

        var data = new SomeData();
        data.name = "test";

        client.runCage(wireMockRuntimeInfo.getHttpBaseUrl(), "test-cage", data, false, "");

        var pattern = postRequestedFor(urlEqualTo(cageNameEndpoint))
                .withoutHeader("x-async")
                .withoutHeader("x-version-id");

        verify(pattern);
    }

    @Test
    void hittingCreateClientSideDecryptTokenEndpointWithExpiryWorksCorrectly(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String createClientSideDecryptTokenEndpoint = "/client-side-tokens";
        var expectedResult = new TokenResult();
        expectedResult.token = "token1234567890";
        expectedResult.expiry = 1234567890;
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(post(urlEqualTo(createClientSideDecryptTokenEndpoint)).willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody("{\"token\":\"token1234567890\", \"expiry\": \"1234567890\"}")
            .withStatus(201)));
        
        var data = new SomeData();
        data.name = "test";

        var result = client.createClientSideToken(wireMockRuntimeInfo.getHttpBaseUrl(), "api:decrypt", data, Instant.now());

        Assertions.assertEquals("token1234567890", result.token);
        Assertions.assertEquals(1234567890, result.expiry);
    }

    @Test
    void hittingCreateClientSideDecryptTokenEndpointWithoutExpiryWorksCorrectly(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String createClientSideTokenEndpoint = "/client-side-tokens";
        var expectedResult = new TokenResult();
        expectedResult.token = "token1234567890";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(post(urlEqualTo(createClientSideTokenEndpoint)).willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody("{\"token\":\"token1234567890\", \"expiry\": \"1234567890\"}")
            .withStatus(201)));
        
        var data = new SomeData();
        data.name = "test";

        var result = client.createClientSideToken(wireMockRuntimeInfo.getHttpBaseUrl(), "api:decrypt", data);

        Assertions.assertEquals("token1234567890", result.token);
        Assertions.assertEquals(1234567890, result.expiry);
    }

    @Test
    void hittingCreateClientSideTokenEndpointThrows(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var client = new HttpHandler(API_KEY, APP_UUID);
        
        var data = new SomeData();
        data.name = "test";

        assertThrows(HttpFailureException.class, () -> client.createClientSideToken(wireMockRuntimeInfo.getHttpBaseUrl(), "api:decrypt", data));
    }

    @Test
    void hittingCreateRunTokenEndpointValidatesBasicHeaders(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String createRunTokenEndpoint = "/v2/functions/test-cage/run-token";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(post(urlEqualTo(createRunTokenEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"result\":{\"message\":\"someMessage\",\"name\":\"someEncryptedData\"},\"runId\":\"s0m3Str1ngW1thNumb3rs\"}")
                .withStatus(200)));

        var data = new SomeData();
        data.name = "test";

        client.createRunToken(wireMockRuntimeInfo.getHttpBaseUrl(), "test-cage", data);

        assertHeadersForCageRun(createRunTokenEndpoint, API_KEY, new HashMap<>());
    }

    @Test
    void hittingCreateRunTokenEndpointWorksCorrectly(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String createRunTokenEndpoint = "/v2/functions/test-cage/run-token";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(post(urlEqualTo(createRunTokenEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"token\":\"s0m3RunT0kenW1thNumb3rs\"}")
                .withStatus(200)));

        var data = new SomeData();
        data.name = "test";

        var result = client.createRunToken(wireMockRuntimeInfo.getHttpBaseUrl(), "test-cage", data);

        Assertions.assertEquals("s0m3RunT0kenW1thNumb3rs", result.token);
    }

    @Test
    void hittingRunTokenEndpointThrows(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var client = new HttpHandler(API_KEY, APP_UUID);

        var data = new SomeData();
        data.name = "test";

        assertThrows(HttpFailureException.class, () -> client.createRunToken(wireMockRuntimeInfo.getHttpBaseUrl(), "test-cage", data));
    }

    @Test
    void hittingDecryptEndpointWorksCorrectly(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        var expectedDecryptResult = new CardData();
        expectedDecryptResult.cardNumber = "4242424242424242";
        expectedDecryptResult.cvv = 123;
        expectedDecryptResult.expiry = "12/24";

        final String decryptEndpoint = "/decrypt";
        var client = new HttpHandler(APP_UUID, API_KEY);
        stubFor(post(urlEqualTo(decryptEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"cardNumber\": \"4242424242424242\", \"cvv\": \"123\", \"expiry\": \"12/24\"}")
                .withStatus(200)));
        
        var dataToDecrypt = new HashMap<String, String>();
        dataToDecrypt.put("cardNumber", "ev:abc123:$");
        dataToDecrypt.put("cvv", "ev:def456:$");
        dataToDecrypt.put("expiry", "12/24");

        CardData result = client.decrypt(wireMockRuntimeInfo.getHttpBaseUrl(), dataToDecrypt, CardData.class);
        Assertions.assertEquals(result.cardNumber, expectedDecryptResult.cardNumber);
        Assertions.assertEquals(result.cvv, expectedDecryptResult.cvv);
        Assertions.assertEquals(result.expiry, expectedDecryptResult.expiry);
    }

    @Test
    void hittingGetOutboundRelayConfigurationEndpointWorksCorrectly(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String getRelayOutboundConfigEndpoint = "/v2/relay-outbound";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(get(urlEqualTo(getRelayOutboundConfigEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"appUuid\":\"app_2c364a9566e4\",\"teamUuid\":\"team_5e71a82322c7\",\"strictMode\":false,\"outboundDestinations\":{\"api.twilio.com\":{\"id\":210,\"appUuid\":\"app_2c364a9566e4\",\"createdAt\":\"2022-11-24T09:01:48.354Z\",\"updatedAt\":\"2022-11-24T09:01:48.354Z\",\"deletedAt\":null,\"routeSpecificFieldsToEncrypt\":[],\"deterministicFieldsToEncrypt\":[],\"encryptEmptyStrings\":true,\"curve\":\"secp256k1\",\"uuid\":\"outbound_destination_ade4771a1ccf\",\"destinationDomain\":\"api.twilio.com\"}}}")
                .withStatus(200)));

        var actual = client.getOutboundRelayConfig(wireMockRuntimeInfo.getHttpBaseUrl());
        Assertions.assertNull(actual.pollInterval);
        Assertions.assertNotNull(actual.config.outboundDestinations);
        Assertions.assertNotNull(actual.config.outboundDestinations.get("api.twilio.com"));
        Assertions.assertEquals("api.twilio.com", actual.config.outboundDestinations.get("api.twilio.com").destinationDomain);

        verify(getRequestedFor(urlEqualTo(getRelayOutboundConfigEndpoint))
                .withHeader("API-KEY", equalTo(API_KEY))
        );
    }

    @Test
    void hittingGetOutboundRelayConfigurationEndpointWorksCorrectlyWhenNoDestinationDomainIsSet(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String getRelayOutboundConfigEndpoint = "/v2/relay-outbound";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(get(urlEqualTo(getRelayOutboundConfigEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"appUuid\":\"app_2c364a9566e4\",\"teamUuid\":\"team_5e71a82322c7\",\"strictMode\":false,\"outboundDestinations\":{}}")
                .withStatus(200)));

        var actual = client.getOutboundRelayConfig(wireMockRuntimeInfo.getHttpBaseUrl());
        Assertions.assertTrue(actual.config.outboundDestinations.isEmpty());

        verify(getRequestedFor(urlEqualTo(getRelayOutboundConfigEndpoint))
                .withHeader("API-KEY", equalTo(API_KEY))
        );
    }

    @Test
    void hittingGetOutboundRelayConfigurationEndpointWorksCorrectlyWhenThePollIntervalResponseHeaderIsSet(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String getRelayOutboundConfigEndpoint = "/v2/relay-outbound";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(get(urlEqualTo(getRelayOutboundConfigEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withHeader("X-Poll-Interval", "5")
                .withBody("{\"appUuid\":\"app_2c364a9566e4\",\"teamUuid\":\"team_5e71a82322c7\",\"strictMode\":false,\"outboundDestinations\":{\"api.twilio.com\":{\"id\":210,\"appUuid\":\"app_2c364a9566e4\",\"createdAt\":\"2022-11-24T09:01:48.354Z\",\"updatedAt\":\"2022-11-24T09:01:48.354Z\",\"deletedAt\":null,\"routeSpecificFieldsToEncrypt\":[],\"deterministicFieldsToEncrypt\":[],\"encryptEmptyStrings\":true,\"curve\":\"secp256k1\",\"uuid\":\"outbound_destination_ade4771a1ccf\",\"destinationDomain\":\"api.twilio.com\"}}}")
                .withStatus(200)));

        var actual = client.getOutboundRelayConfig(wireMockRuntimeInfo.getHttpBaseUrl());
        Assertions.assertNotNull(actual.config.outboundDestinations);
        Assertions.assertNotNull(actual.config.outboundDestinations.get("api.twilio.com"));
        Assertions.assertEquals("api.twilio.com", actual.config.outboundDestinations.get("api.twilio.com").destinationDomain);

        verify(getRequestedFor(urlEqualTo(getRelayOutboundConfigEndpoint))
                .withHeader("API-KEY", equalTo(API_KEY))
        );
    }

    @Test
    void hittingGetOutboundRelayConfigurationEndpointDoesNotThrowWhenThePollIntervalResponseHeaderIsInvalid(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String getRelayOutboundConfigEndpoint = "/v2/relay-outbound";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(get(urlEqualTo(getRelayOutboundConfigEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withHeader("X-Poll-Interval", "invalid")
                .withBody("{\"appUuid\":\"app_2c364a9566e4\",\"teamUuid\":\"team_5e71a82322c7\",\"strictMode\":false,\"outboundDestinations\":{\"api.twilio.com\":{\"id\":210,\"appUuid\":\"app_2c364a9566e4\",\"createdAt\":\"2022-11-24T09:01:48.354Z\",\"updatedAt\":\"2022-11-24T09:01:48.354Z\",\"deletedAt\":null,\"routeSpecificFieldsToEncrypt\":[],\"deterministicFieldsToEncrypt\":[],\"encryptEmptyStrings\":true,\"curve\":\"secp256k1\",\"uuid\":\"outbound_destination_ade4771a1ccf\",\"destinationDomain\":\"api.twilio.com\"}}}")
                .withStatus(200)));

        var actual = client.getOutboundRelayConfig(wireMockRuntimeInfo.getHttpBaseUrl());
        Assertions.assertNotNull(actual.config.outboundDestinations);
        Assertions.assertNotNull(actual.config.outboundDestinations.get("api.twilio.com"));
        Assertions.assertEquals("api.twilio.com", actual.config.outboundDestinations.get("api.twilio.com").destinationDomain);

        verify(getRequestedFor(urlEqualTo(getRelayOutboundConfigEndpoint))
                .withHeader("API-KEY", equalTo(API_KEY))
        );
    }

    @Test
    void hittingGetOutboundRelayConfigurationEndpointDoesNotThrowWhenThePollIntervalResponseHeaderIsNotSet(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String getRelayOutboundConfigEndpoint = "/v2/relay-outbound";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(get(urlEqualTo(getRelayOutboundConfigEndpoint)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withHeader("X-Poll-Interval", "")
                .withBody("{\"appUuid\":\"app_2c364a9566e4\",\"teamUuid\":\"team_5e71a82322c7\",\"strictMode\":false,\"outboundDestinations\":{\"api.twilio.com\":{\"id\":210,\"appUuid\":\"app_2c364a9566e4\",\"createdAt\":\"2022-11-24T09:01:48.354Z\",\"updatedAt\":\"2022-11-24T09:01:48.354Z\",\"deletedAt\":null,\"routeSpecificFieldsToEncrypt\":[],\"deterministicFieldsToEncrypt\":[],\"encryptEmptyStrings\":true,\"curve\":\"secp256k1\",\"uuid\":\"outbound_destination_ade4771a1ccf\",\"destinationDomain\":\"api.twilio.com\"}}}")
                .withStatus(200)));

        var actual = client.getOutboundRelayConfig(wireMockRuntimeInfo.getHttpBaseUrl());
        Assertions.assertNotNull(actual.config.outboundDestinations);
        Assertions.assertNotNull(actual.config.outboundDestinations.get("api.twilio.com"));
        Assertions.assertEquals("api.twilio.com", actual.config.outboundDestinations.get("api.twilio.com").destinationDomain);

        verify(getRequestedFor(urlEqualTo(getRelayOutboundConfigEndpoint))
                .withHeader("API-KEY", equalTo(API_KEY))
        );
    }

    @Test
    void hittingGetOutboundRelayConfigurationEndpointThrows(WireMockRuntimeInfo wireMockRuntimeInfo) {
        final String getRelayOutboundConfigEndpoint = "/v2/relay-outbound";
        var client = new HttpHandler(API_KEY, APP_UUID);

        stubFor(get(urlEqualTo(getRelayOutboundConfigEndpoint)).willReturn(aResponse()
                .withStatus(500)));

        assertThrows(HttpFailureException.class, () -> client.getOutboundRelayConfig(wireMockRuntimeInfo.getHttpBaseUrl()));

        verify(getRequestedFor(urlEqualTo(getRelayOutboundConfigEndpoint))
                .withHeader("API-KEY", equalTo(API_KEY))
        );
    }
}
