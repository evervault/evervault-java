package com.evervault.utils;

import com.evervault.contracts.IProvideDecryptionAndIgnoreDomains;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;

import java.util.Arrays;
import java.util.function.Predicate;

public class ProxyRoutePlanner {
    public static HttpRoutePlanner getOutboundRelayRoutePlanner(IProvideDecryptionAndIgnoreDomains configProvider) {
        return buildRoutePlanner(hostname -> {
                return !Arrays.stream(configProvider.getAlwaysIgnoreDomainRegexes())
                    .anyMatch(pattern -> pattern.matcher(hostname).matches())
                    && Arrays.stream(configProvider.getDecryptionDomainRegexes())
                    .anyMatch(pattern -> pattern.matcher(hostname).matches());
            });
    }

    private static HttpRoutePlanner buildRoutePlanner(Predicate<String> shouldDomainBeDecryptedPredicate) {
        HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(ProxySystemSettings.PROXY_HOST) {
            public HttpRoute determineRoute(
                    final HttpHost host,
                    final HttpRequest request,
                    final HttpContext context) throws HttpException {
                String hostname = host.getHostName();

                boolean decrypt = shouldDomainBeDecryptedPredicate.test(hostname);
                if (!decrypt) {
                    // Return direct route
                    return new HttpRoute(host);
                }
                return super.determineRoute(host, request, context);
            }
        };

        return routePlanner;
    }
}
