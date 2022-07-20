package com.evervault.utils;

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

    public static HttpRoutePlanner getEvervaultRoutePlanner(String[] ignoreDomains) {
        return buildEvervaultRoutePlanner(new Predicate<String>() {
            @Override
            public boolean test(String hostname) {
                return !Arrays.asList(ignoreDomains).contains(hostname);
            }
        });
    }

    public static HttpRoutePlanner getEvervaultRoutePlannerV2(String[] decryptionDomains, String[] alwaysIgnoreDomains) {
        return buildEvervaultRoutePlanner(new Predicate<String>() {
            @Override
            public boolean test(String hostname) {
                if (Arrays.asList(alwaysIgnoreDomains).contains(hostname))
                    return false;
                return Arrays.stream(decryptionDomains).anyMatch(domain -> domain.equals(hostname) || domain.charAt(0) == '*' && hostname.endsWith(domain.substring(1)));
            }
        });
    }

    private static HttpRoutePlanner buildEvervaultRoutePlanner(Predicate<String> shouldDomainBeDecryptedPredicate) {
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
