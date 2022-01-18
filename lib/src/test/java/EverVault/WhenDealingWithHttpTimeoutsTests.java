package EverVault;

import EverVault.Services.HttpHandler;
import EverVault.Services.ResourceControl;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.eclipse.jetty.util.resource.Resource;
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

    @Test
    void blocksResourceAfterReachingLimit() {
        var resourceControl = new ResourceControl();

        for (int i = 0; i < 3; i++ ) {
            resourceControl.timeOutOccurred();
        }

        assert resourceControl.getBlocked();
    }

    @Test
    void releasesItAfterTimeCounterEnds() throws InterruptedException {
        var resourceControl = new ResourceControl(1, 10, null);

        resourceControl.timeOutOccurred();

        assert resourceControl.getBlocked();

        Thread.currentThread().wait(20);

        assert !resourceControl.getBlocked();
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
