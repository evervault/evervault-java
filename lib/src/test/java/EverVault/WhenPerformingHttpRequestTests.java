package EverVault;

import EverVault.Exceptions.HttpFailureException;
import EverVault.ReadModels.CageRunResult;
import EverVault.Services.HttpApiRepository;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.internal.LinkedTreeMap;
import org.junit.jupiter.api.Test;

import javax.naming.Name;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
public class WhenPerformingHttpRequestTests {
    private static final String USER_AGENT_HEADER = "evervault-java/1.0";
    private static final String CONTENT_TYPE = "application/json";
    private static final String API_KEY = "Foo";
    private static final String RAW_TEXT_CAGES_KEY_ENDPOINT = "{\"teamUuid\":\"de7350990fd7\",\"key\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo7+jkmJ1uZsmiA5omE96RaepPYj2J6DzlE0DNWPoVZZNVb/ShqxSA4zKfE9Kh4MuI6fKpg0/pMhf8Re398ac9s2xKsjDvQHOhLLOfmgcrQgZyLGvdsrllcb1JY8kLNTdgONpn3S/BQetdEPG7oFp1RRIw60Iyy+v2R+r092zItbqLUpb0Vpu2z2uMxylZFc33VuDVIFF+fc9vE0gVPFoHezZ+1+EmqiJdkH/1GcPoVswzCvg3djmCo3Zhx3GdiB464GOl2ZlujwSN9dPkFhndIUZYK9iJhlcItyGkKH1OV/HAl8k2u/7pKUDLFe4lMWX9yASuj6y3CLdrPcbAuky3QIDAQAB\",\"ecdhKey\":\"AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr\"}";

    private void assertHeaders(String endpoint, String apiKey, HashMap<String, String> headerMap) {
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

    @Test
    public void httpHeadersAreIncludedForGets(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException, InterruptedException, HttpFailureException {
        final String endpoint = "/Foo";

        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(RAW_TEXT_CAGES_KEY_ENDPOINT)));

        var client = new HttpApiRepository(API_KEY);

        final var urlPath = wireMockRuntimeInfo.getHttpBaseUrl() + endpoint;

        client.getCagePublicKeyFromEndpoint(urlPath);

        assertHeaders(endpoint, API_KEY, new HashMap<>());
    }

    @Test
    void additionalHeadersAreIncluded(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException, InterruptedException, HttpFailureException {
        final String endpoint = "/Foo";

        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(RAW_TEXT_CAGES_KEY_ENDPOINT)));

        var client = new HttpApiRepository(API_KEY);

        final var urlPath = wireMockRuntimeInfo.getHttpBaseUrl() + "/Foo";

        var headerMap = new HashMap<String, String>();
        headerMap.put("Foo", "Bar");
        headerMap.put("Bar", "Foo");

        client.getCagePublicKeyFromEndpoint(urlPath, headerMap);

        assertHeaders(endpoint, API_KEY, headerMap);
    }

    @Test
    void hittingCagePublicKeyEndpointParsesItCorrectly(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException, InterruptedException, HttpFailureException {
        var client = new HttpApiRepository(API_KEY);
        final var urlPath = wireMockRuntimeInfo.getHttpBaseUrl() + "/cages/key";

        stubFor(get(urlEqualTo("/cages/key"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(RAW_TEXT_CAGES_KEY_ENDPOINT)));

        var cagesKey = client.getCagePublicKeyFromEndpoint(urlPath);

        assertEquals(cagesKey.key, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo7+jkmJ1uZsmiA5omE96RaepPYj2J6DzlE0DNWPoVZZNVb/ShqxSA4zKfE9Kh4MuI6fKpg0/pMhf8Re398ac9s2xKsjDvQHOhLLOfmgcrQgZyLGvdsrllcb1JY8kLNTdgONpn3S/BQetdEPG7oFp1RRIw60Iyy+v2R+r092zItbqLUpb0Vpu2z2uMxylZFc33VuDVIFF+fc9vE0gVPFoHezZ+1+EmqiJdkH/1GcPoVswzCvg3djmCo3Zhx3GdiB464GOl2ZlujwSN9dPkFhndIUZYK9iJhlcItyGkKH1OV/HAl8k2u/7pKUDLFe4lMWX9yASuj6y3CLdrPcbAuky3QIDAQAB");
        assertEquals(cagesKey.ecdhKey, "AhmiyfX6dVt1IML5qF+giWEdCaX60oQE+d9b2FXOSOXr");
        assertEquals(cagesKey.teamUuid, "de7350990fd7");
    }

    @Test
    void httpStatusNotOkMustThrow(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var client = new HttpApiRepository(API_KEY);
        final var urlPath = wireMockRuntimeInfo.getHttpBaseUrl() + "/cages/key";

        assertThrows(HttpFailureException.class, () -> client.getCagePublicKeyFromEndpoint(urlPath));
    }

    private static class SomeData implements Serializable {
        public String name;
    }

    private static class NameData implements Serializable {
        public String message;
        public String runId;
    }

    @Test
    void hittingCageRunEndpointWorksCorrectly(WireMockRuntimeInfo wireMockRuntimeInfo) throws HttpFailureException, IOException, InterruptedException {
        final String cageName = "/test-cage";
        var client = new HttpApiRepository(API_KEY);

        stubFor(post(urlEqualTo(cageName)).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"result\":{\"message\":\"someMessage\",\"name\":\"someEncryptedData\"},\"runId\":\"s0m3Str1ngW1thNumb3rs\"}")
                .withStatus(200)));

        var data = new SomeData();
        data.name = "test";

        var result = client.runCage(wireMockRuntimeInfo.getHttpBaseUrl(), "test-cage", data, false, "1.0.0");

        assertEquals("s0m3Str1ngW1thNumb3rs", result.runId);

//        var map = (LinkedTreeMap<String, String>)result.result;
//        assertEquals("someMessage", map.get("message"));
//        assertEquals("someEncryptedData", map.get("name"));
    }
}
