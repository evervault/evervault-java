package com.evervault.utils;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.nio.charset.StandardCharsets;

public class ProxyCredentialsProvider {

    public static CredentialsProvider getEvervaultCredentialsProvider(String proxyHost, Integer proxyPort, String teamUuid, String teamApiKey) {
        AuthScope authScope= new AuthScope(proxyHost, proxyPort);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(teamUuid, teamApiKey);

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(authScope, credentials);
        return credentialsProvider;
    }
}
