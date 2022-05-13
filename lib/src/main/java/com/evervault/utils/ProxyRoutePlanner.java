package com.evervault.utils;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;

import java.util.Arrays;

public class ProxyRoutePlanner {

    public static HttpRoutePlanner getEvervaultRoutePlanner(String[] ignoreDomains) {

        HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(ProxySystemSettings.PROXY_HOST) {
            public HttpRoute determineRoute(
                    final HttpHost host,
                    final HttpRequest request,
                    final HttpContext context) throws HttpException {
                String hostname = host.getHostName();

                boolean ignoredDomain = Arrays.stream(ignoreDomains).anyMatch(hostname::equals);
                if (ignoredDomain) {
                    // Return direct route
                    return new HttpRoute(host);
                }
                return super.determineRoute(host, request, context);
            }
        };

        return routePlanner;
    }
}
