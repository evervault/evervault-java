package com.evervault;

import com.evervault.contracts.*;
import com.evervault.exceptions.EvervaultException;
import com.evervault.exceptions.HttpFailureException;
import com.evervault.services.EvervaultService;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class WhenUsingDecriptionDomains {

    private IProvideOutboundRelayConfigFromHttpApi outboundRelayConfigProvider = mock(IProvideOutboundRelayConfigFromHttpApi.class);

    private class Evervault extends EvervaultService {
        private String[] ignoreDomains = new String[0];

        public void setupWrapper(IProvideOutboundRelayConfigFromHttpApi outboundRelayConfigProvider) throws EvervaultException {
            this.setupOutboundRelayConfigProvider(outboundRelayConfigProvider);
        }

        public void setupDecryptionDomains(String[] decryptionDomains, String[] ignoreDomains) throws EvervaultException {
            this.setupIntercept(decryptionDomains, ignoreDomains);
        }
    }

    @BeforeEach
    public void setup() {
        reset(outboundRelayConfigProvider);
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllMatchingDomainsAreRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.setupDecryptionDomains(new String[]{"example.com"}, new String[0]);

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals("strict.relay.evervault.com", proxyHost.getHostName());
        verify(outboundRelayConfigProvider, never()).getOutboundRelayConfig(any());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllMatchingOneLevelSubDomainsAreRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.setupDecryptionDomains(new String[]{"*.example.com"}, new String[0]);

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("hey.example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals("strict.relay.evervault.com", proxyHost.getHostName());
        verify(outboundRelayConfigProvider, never()).getOutboundRelayConfig(any());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllMatchingSubDomainsAreRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.setupDecryptionDomains(new String[]{"*.example.com"}, new String[0]);

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("hello.hey.example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals("strict.relay.evervault.com", proxyHost.getHostName());
        verify(outboundRelayConfigProvider, never()).getOutboundRelayConfig(any());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllDomainsAreRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.setupDecryptionDomains(new String[]{"**"}, new String[0]);

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertEquals("strict.relay.evervault.com", proxyHost.getHostName());
        verify(outboundRelayConfigProvider, never()).getOutboundRelayConfig(any());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllNonMatchingDomainsAreNotRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.setupDecryptionDomains(new String[]{"example.com"}, new String[0]);

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("anotherexample.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertNull(proxyHost);
        verify(outboundRelayConfigProvider, never()).getOutboundRelayConfig(any());
    }

    @Test
    public void shouldSetupRoutePlannerSoThatAllIgnoredDomainsAreNotRoutedToRelayProxy() throws EvervaultException, HttpFailureException, IOException, InterruptedException, HttpException {
        // When
        var evervault = new Evervault();
        evervault.setupWrapper(outboundRelayConfigProvider);
        evervault.setupDecryptionDomains(new String[]{"**"}, new String[]{"example.com"});

        // Then
        var httpRoutePlanner = evervault.getEvervaultHttpRoutePlanner();
        HttpHost proxyHost = httpRoutePlanner.determineRoute(new HttpHost("example.com"), mockHttpRequest(), mockHttpContext()).getProxyHost();
        assertNull(proxyHost);
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
