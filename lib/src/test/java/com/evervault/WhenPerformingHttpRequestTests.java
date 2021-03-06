package com.evervault;

import com.evervault.exceptions.HttpFailureException;
import com.evervault.services.HttpHandler;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.internal.LinkedTreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
public class WhenPerformingHttpRequestTests {
    private static final String USER_AGENT_HEADER = "evervault-java/1.0";
    private static final String CONTENT_TYPE = "application/json";
    private static final String API_KEY = "Foo";
    private static final String RAW_TEXT_CAGES_KEY_ENDPOINT = "{\"teamUuid\":\"de7350990fd7\",\"key\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo7+jkmJ1uZsmiA5omE96RaepPYj2J6DzlE0DNWPoVZZNVb/ShqxSA4zKfE9Kh4MuI6fKpg0/pMhf8Re398ac9s2xKsjDvQHOhLLOfmgcrQgZyLGvdsrllcb1JY8kLNTdgONpn3S/BQetdEPG7oFp1RRIw60Iyy+v2R+r092zItbqLUpb0Vpu2z2uMxylZFc33VuDVIFF+fc9vE0gVPFoHezZ+1+EmqiJdkH/1GcPoVswzCvg3djmCo3Zhx3GdiB464GOl2ZlujwSN9dPkFhndIUZYK9iJhlcItyGkKH1OV/HAl8k2u/7pKUDLFe4lMWX9yASuj6y3CLdrPcbAuky3QIDAQAB\",\"ecdhKey\":\"AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr\"}";

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

        var client = new HttpHandler(API_KEY);

        client.getCagePublicKeyFromEndpoint(wireMockRuntimeInfo.getHttpBaseUrl() + "/");
    }

    @Test
    public void runCageUrlEndingWithSlashDoesNotThrow(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String cageNameEndpoint = "/test-cage";
        var client = new HttpHandler(API_KEY);

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

        var client = new HttpHandler(API_KEY);

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

        var client = new HttpHandler(API_KEY);

        var headerMap = new HashMap<String, String>();
        headerMap.put("Foo", "Bar");
        headerMap.put("Bar", "Foo");

        client.getCagePublicKeyFromEndpoint(wireMockRuntimeInfo.getHttpBaseUrl(), headerMap);

        assertHeadersForCageKey(endpoint, API_KEY, headerMap);
    }

    @Test
    void hittingCagePublicKeyEndpointParsesItCorrectly(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException, InterruptedException, HttpFailureException {
        var client = new HttpHandler(API_KEY);
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
        var client = new HttpHandler(API_KEY);
        final var urlPath = wireMockRuntimeInfo.getHttpBaseUrl() + "/cages/key";

        assertThrows(HttpFailureException.class, () -> client.getCagePublicKeyFromEndpoint(urlPath));
    }

    private static class SomeData implements Serializable {
        public String name;
    }

    @Test
    void hittingCageRunEndpointValidatesBasicHeaders(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String cageNameEndpoint = "/test-cage";
        var client = new HttpHandler(API_KEY);

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
        var client = new HttpHandler(API_KEY);

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
        var client = new HttpHandler(API_KEY);

        var data = new SomeData();
        data.name = "test";

        assertThrows(HttpFailureException.class, () -> client.runCage(wireMockRuntimeInfo.getHttpBaseUrl(), "test-cage", data, false, "1.0.0"));
    }

    @Test
    void validatesAsyncTrueHeaderWhenHittingCageRunEndpoint(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String cageNameEndpoint = "/test-cage";
        var client = new HttpHandler(API_KEY);

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
        var client = new HttpHandler(API_KEY);

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
        var client = new HttpHandler(API_KEY);

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
}
