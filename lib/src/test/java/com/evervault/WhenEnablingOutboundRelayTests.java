package com.evervault;

import com.evervault.contracts.*;
import com.evervault.exceptions.EvervaultException;
import com.evervault.exceptions.HttpFailureException;
import com.evervault.models.OutboundRelayConfigResult;
import com.evervault.services.CachedOutboundRelayConfigService;
import com.evervault.services.EvervaultService;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class WhenEnablingOutboundRelayTests {

    private IProvideOutboundRelayConfigFromHttpApi outboundRelayConfigProvider = mock(IProvideOutboundRelayConfigFromHttpApi.class);

    private class Evervault extends EvervaultService {
        public void setupWrapper(IProvideOutboundRelayConfigFromHttpApi outboundRelayConfigProvider) throws EvervaultException {
            this.setupOutboundRelayConfigProvider(outboundRelayConfigProvider);
        }

        public void enableOutboundRelay() throws EvervaultException {
            this.setupIntercept(null);
        }

        public void setupDecryptionDomains(String[] decryptionDomains) throws EvervaultException {
            this.setupIntercept(decryptionDomains);
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
        var destinationDomains = new HashMap<String, OutboundRelayConfigResult.OuboundDestination>();
        destinationDomains.put("example.com", new OutboundRelayConfigResult.OuboundDestination("example.com"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(new OutboundRelayConfigResult(destinationDomains));

        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.enableOutboundRelay();

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals("strict.relay.evervault.com", proxyHost.getHostName());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllMatchingOneLevelSubDomainsAreRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // Given
        var destinationDomains = new HashMap<String, OutboundRelayConfigResult.OuboundDestination>();
        destinationDomains.put("*.example.com", new OutboundRelayConfigResult.OuboundDestination("*.example.com"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(new OutboundRelayConfigResult(destinationDomains));

        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.enableOutboundRelay();

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("hey.example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals("strict.relay.evervault.com", proxyHost.getHostName());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllMatchingSubDomainsAreRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // Given
        var destinationDomains = new HashMap<String, OutboundRelayConfigResult.OuboundDestination>();
        destinationDomains.put("*.example.com", new OutboundRelayConfigResult.OuboundDestination("*.example.com"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(new OutboundRelayConfigResult(destinationDomains));

        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.enableOutboundRelay();

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("hello.hey.example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals("strict.relay.evervault.com", proxyHost.getHostName());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllDomainsAreRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // Given
        var destinationDomains = new HashMap<String, OutboundRelayConfigResult.OuboundDestination>();
        destinationDomains.put("**", new OutboundRelayConfigResult.OuboundDestination("**"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(new OutboundRelayConfigResult(destinationDomains));

        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.enableOutboundRelay();

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals("strict.relay.evervault.com", proxyHost.getHostName());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllNonMatchingDomainsAreNotRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // Given
        var destinationDomains = new HashMap<String, OutboundRelayConfigResult.OuboundDestination>();
        destinationDomains.put("example.com", new OutboundRelayConfigResult.OuboundDestination("example.com"));
        when(outboundRelayConfigProvider.getOutboundRelayConfig(any())).thenReturn(new OutboundRelayConfigResult(destinationDomains));

        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.enableOutboundRelay();

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("anotherexample.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertNull(proxyHost);
    }

    @Test
    public void shouldSetupRoutePlannerButWithoutCallingTheApi() throws EvervaultException, HttpException, HttpFailureException, IOException, InterruptedException {
        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.setupDecryptionDomains(new String[]{"example.com"});

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals("strict.relay.evervault.com", proxyHost.getHostName());
        verify(outboundRelayConfigProvider, never()).getOutboundRelayConfig(any());
    }

    private HttpRequest mockHttpRequest() {
        var httpRequest = mock(HttpRequest.class);
        return httpRequest;
    }

    private HttpContext mockHttpContext() {
        var httpContext = mock(HttpContext.class);
        return httpContext;
    }

}
