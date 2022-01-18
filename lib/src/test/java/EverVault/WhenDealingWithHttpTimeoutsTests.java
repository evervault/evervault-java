package EverVault;

import EverVault.Services.HttpHandler;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.net.http.HttpTimeoutException;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
public class WhenDealingWithHttpTimeoutsTests {
    private static final String API_KEY = "Foo";

    @Test
    void triggersExceptionWhenHittingPublicKeyEndpoint(WireMockRuntimeInfo wireMockRuntimeInfo) {
        final String endpoint = "/Foo";

        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1)));

        var client = new HttpHandler(API_KEY, Duration.ofMillis(10));
        assertThrows(HttpTimeoutException.class, () -> client.getCagePublicKeyFromEndpoint(wireMockRuntimeInfo.getHttpBaseUrl() + endpoint));
    }

    @Test
    void triggersExceptionWhenHittingRunCage(WireMockRuntimeInfo wireMockRuntimeInfo) {
        final String endpoint = "/Foo";

        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1)));

        var client = new HttpHandler(API_KEY, Duration.ofMillis(10));

        assertThrows(HttpTimeoutException.class, () -> client.runCage(wireMockRuntimeInfo.getHttpBaseUrl(), "Foo", "Foo", true, null));
    }

//    @Test
//    void retryWhenExceptionComesUp(WireMockRuntimeInfo wireMockRuntimeInfo) {
//        final String endpoint = "/Foo";
//
//        stubFor(get(urlEqualTo(endpoint))
//                .willReturn(aResponse()
//                        .withHeader("Content-Type", "application/json")
//                        .withFixedDelay(1)));
//
//        var client = new HttpHandler(API_KEY, Duration.ofMillis(10));
//
//        client.getCagePublicKeyFromEndpoint(wireMockRuntimeInfo.getHttpBaseUrl() + endpoint)
//    }
}
