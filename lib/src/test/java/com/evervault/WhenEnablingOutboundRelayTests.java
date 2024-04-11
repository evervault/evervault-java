package com.evervault;

import com.evervault.TestUtils.RelayHostResolver;
import com.evervault.contracts.*;
import com.evervault.exceptions.EvervaultException;
import com.evervault.exceptions.HttpFailureException;
import com.evervault.models.OutboundRelayConfigResult;
import com.evervault.services.CachedOutboundRelayConfigService;
import com.evervault.services.EvervaultService;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class WhenEnablingOutboundRelayTests {

    private IProvideOutboundRelayConfigFromHttpApi outboundRelayConfigProvider = mock(IProvideOutboundRelayConfigFromHttpApi.class);
    private IScheduleRepeatableTask repeatableTaskScheduler = mock(IScheduleRepeatableTask.class);

    private class Evervault extends EvervaultService {
        private String[] ignoreDomains = new String[0];

        public void setupWrapper(IProvideOutboundRelayConfigFromHttpApi outboundRelayConfigProvider, IScheduleRepeatableTask repeatableTaskScheduler) throws EvervaultException {
            this.setupOutboundRelayConfigProvider(outboundRelayConfigProvider);
            this.setupRepeatableTaskScheduler(repeatableTaskScheduler);
        }

        public void setupIgnoreDomains(String[] ignoreDomains) {
            this.ignoreDomains = ignoreDomains;
        }

        public void enableOutboundRelay() throws EvervaultException {
            this.setupIntercept(null, ignoreDomains);
        }

        public void setupDecryptionDomains(String[] decryptionDomains) throws EvervaultException {
            this.setupIntercept(decryptionDomains, ignoreDomains);
        }
    }

    @BeforeEach
    public void setup() {
        CachedOutboundRelayConfigService.clearCache();
        reset(outboundRelayConfigProvider);
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllMatchingDomainsAreRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // Given
        HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination> destinationDomains = new HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination>();
        destinationDomains.put("example.com", new OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination("example.com"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(new OutboundRelayConfigResult(null, new OutboundRelayConfigResult.OutboundRelayConfig(destinationDomains)));

        // When
        Evervault evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider, repeatableTaskScheduler);
        evervault.enableOutboundRelay();

        // Then
        HttpRoutePlanner httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals(RelayHostResolver.getRelayHost(), proxyHost.getHostName());
        verify(repeatableTaskScheduler, times(1)).schedule(any());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllMatchingOneLevelSubDomainsAreRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // Given
        HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination> destinationDomains = new HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination>();
        destinationDomains.put("*.example.com", new OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination("*.example.com"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(new OutboundRelayConfigResult(null, new OutboundRelayConfigResult.OutboundRelayConfig(destinationDomains)));

        // When
        Evervault evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider, repeatableTaskScheduler);
        evervault.enableOutboundRelay();

        // Then
        HttpRoutePlanner httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("hey.example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals(RelayHostResolver.getRelayHost(), proxyHost.getHostName());
        verify(repeatableTaskScheduler, times(1)).schedule(any());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllMatchingSubDomainsAreRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // Given
        HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination> destinationDomains = new HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination>();
        destinationDomains.put("*.example.com", new OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination("*.example.com"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(new OutboundRelayConfigResult(null, new OutboundRelayConfigResult.OutboundRelayConfig(destinationDomains)));

        // When
        Evervault evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider, repeatableTaskScheduler);
        evervault.enableOutboundRelay();

        // Then
        HttpRoutePlanner httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("hello.hey.example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals(RelayHostResolver.getRelayHost(), proxyHost.getHostName());
        verify(repeatableTaskScheduler, times(1)).schedule(any());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllNonMatchingDomainsAreNotRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // Given
        HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination> destinationDomains = new HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination>();
        destinationDomains.put("example.com", new OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination("example.com"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(new OutboundRelayConfigResult(null, new OutboundRelayConfigResult.OutboundRelayConfig(destinationDomains)));

        // When
        Evervault evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider, repeatableTaskScheduler);
        evervault.enableOutboundRelay();

        // Then
        HttpRoutePlanner httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("anotherexample.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertNull(proxyHost);
        verify(repeatableTaskScheduler, times(1)).schedule(any());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatIgnoredDomainsAreNotRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // Given
        HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination> destinationDomains = new HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination>();
        destinationDomains.put("**", new OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination("**"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(new OutboundRelayConfigResult(null, new OutboundRelayConfigResult.OutboundRelayConfig(destinationDomains)));

        // When
        Evervault evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider, repeatableTaskScheduler);
        evervault.setupIgnoreDomains(new String[]{"example.com"});
        evervault.enableOutboundRelay();

        // Then
        HttpRoutePlanner httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertNull(proxyHost);
        verify(repeatableTaskScheduler, times(1)).schedule(any());
    }

    @Test
    public void shouldSetupRoutePlannerButWithoutCallingTheApi() throws EvervaultException, HttpException, HttpFailureException, IOException, InterruptedException {
        // When
        Evervault evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider, repeatableTaskScheduler);
        evervault.setupDecryptionDomains(new String[]{"example.com"});

        // Then
        HttpRoutePlanner httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals(RelayHostResolver.getRelayHost(), proxyHost.getHostName());
        verify(outboundRelayConfigProvider, never()).getOutboundRelayConfig(any());
    }

    @Test
    void executingTheGetOuboundDelayConfigTaskShouldUpdateTheLocalConfigCacheAndNotUpdateTheDelayForNextExecutionIfNotSpecifiedByApi() throws Exception {
        // Given
        int delay = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        String evervaultApiUrl = "http://api.evervault.com";
        CachedOutboundRelayConfigService.GetOutboundRelayConfigTask task = new CachedOutboundRelayConfigService.GetOutboundRelayConfigTask(delay, timeUnit, outboundRelayConfigProvider, evervaultApiUrl);
        HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination> destinationDomains = new HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination>();
        destinationDomains.put("example.com", new OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination("example.com"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(
                new OutboundRelayConfigResult(null, new OutboundRelayConfigResult.OutboundRelayConfig(destinationDomains))
        );

        // When
        task.execute();

        // Then
        String[] decryptionDomains = (new CachedOutboundRelayConfigService(outboundRelayConfigProvider, repeatableTaskScheduler, evervaultApiUrl, new String[] {})).getDecryptionDomains();
        assertEquals(1, decryptionDomains.length);
        assertEquals("example.com", decryptionDomains[0]);
        assertEquals(delay, task.getDelay());
        assertEquals(timeUnit, task.getTimeUnit());
    }

    @Test
    void executingTheGetOuboundDelayConfigTaskShouldUpdateTheLocalConfigCacheAndUpdateTheDelayForNextExecutionIfSpecifiedByApi() throws Exception {
        // Given
        int delay = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        String evervaultApiUrl = "http://api.evervault.com";
        CachedOutboundRelayConfigService.GetOutboundRelayConfigTask task = new CachedOutboundRelayConfigService.GetOutboundRelayConfigTask(delay, timeUnit, outboundRelayConfigProvider, evervaultApiUrl);
        HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination> destinationDomains = new HashMap<String, OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination>();
        destinationDomains.put("example.com", new OutboundRelayConfigResult.OutboundRelayConfig.OutboundDestination("example.com"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(
                new OutboundRelayConfigResult(10, new OutboundRelayConfigResult.OutboundRelayConfig(destinationDomains))
        );

        // When
        task.execute();

        // Then
        String[] decryptionDomains = (new CachedOutboundRelayConfigService(outboundRelayConfigProvider, repeatableTaskScheduler, evervaultApiUrl, new String[] {})).getDecryptionDomains();
        assertEquals(1, decryptionDomains.length);
        assertEquals("example.com", decryptionDomains[0]);
        assertEquals(10, task.getDelay());
        assertEquals(timeUnit, task.getTimeUnit());
    }

    private HttpRequest mockHttpRequest() {
        HttpRequest httpRequest = mock(HttpRequest.class);
        return httpRequest;
    }

    private HttpContext mockHttpContext() {
        HttpContext httpContext = mock(HttpContext.class);
        return httpContext;
    }

}
