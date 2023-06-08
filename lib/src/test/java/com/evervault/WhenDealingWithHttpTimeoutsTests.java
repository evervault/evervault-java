package com.evervault;

import com.evervault.services.HttpHandler;
import com.evervault.services.ResourceControl;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.Future;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
public class WhenDealingWithHttpTimeoutsTests {
    private static final String API_KEY = "Foo";
    private static final String APP_UUID = "Bar";

    @Test
    void triggersExceptionWhenHittingPublicKeyEndpoint(WireMockRuntimeInfo wireMockRuntimeInfo) {
        final String endpoint = "/Foo";

        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1)));

        var client = new HttpHandler(API_KEY, APP_UUID, Duration.ofMillis(10));
        assertThrows(HttpTimeoutException.class, () -> client.getCagePublicKeyFromEndpoint(wireMockRuntimeInfo.getHttpBaseUrl() + endpoint));
    }

    @Test
    void triggersExceptionWhenHittingRunCage(WireMockRuntimeInfo wireMockRuntimeInfo) {
        final String endpoint = "/Foo";

        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1)));

        var client = new HttpHandler(API_KEY, APP_UUID, Duration.ofMillis(10));

        assertThrows(HttpTimeoutException.class, () -> client.runCage(wireMockRuntimeInfo.getHttpBaseUrl(), "Foo", "Foo", true, null));
    }

    @Test
    @Timeout(1000)
    void blocksResourceAfterReachingLimit() {
        var resourceControl = new ResourceControl(0, 3000);

        resourceControl.timeOutOccurred();

        assert resourceControl.getBlocked();
    }

    @Test
    @Timeout(1000)
    void releasesItAfterTimeCounterEnds() throws InterruptedException {
        var resourceControl = new ResourceControl(0, 100);

        resourceControl.timeOutOccurred();

        assert resourceControl.getBlocked();

        while (resourceControl.getBlocked()) {
            Thread.sleep(10);
        }
    }

    private class CustomResourceControl extends ResourceControl {
        public CustomResourceControl(int countLimit, long timeLimit){
            super(countLimit, timeLimit);
        }

        public int getCounter() {
            return counter;
        }

        public Future getResetTask() {
            return resetTask;
        }
    }

    @Test
    @Timeout(1000)
    void doesNotLaunchAnotherTaskAfterReachedLimit() {
        var resourceControl = new CustomResourceControl(0, 300);

        resourceControl.timeOutOccurred();

        assertEquals(0, resourceControl.getCounter());

        var originalTimer = resourceControl.getResetTask().hashCode();

        resourceControl.timeOutOccurred();
        assertEquals(0, resourceControl.getCounter());

        var secondTimer = resourceControl.getResetTask().hashCode();

        assertEquals(originalTimer, secondTimer);
    }

    @Test
    @Timeout(1000)
    void launchAnotherTaskWhenFirstOneIsDone() throws InterruptedException {
        var resourceControl = new CustomResourceControl(0, 100);

        resourceControl.timeOutOccurred();

        var originalTimer = resourceControl.getResetTask().hashCode();

        while (resourceControl.getBlocked()) {
            Thread.sleep(10);
        }

        resourceControl.timeOutOccurred();

        var secondTimer = resourceControl.getResetTask().hashCode();

        assertNotEquals(originalTimer, secondTimer);
    }
}
