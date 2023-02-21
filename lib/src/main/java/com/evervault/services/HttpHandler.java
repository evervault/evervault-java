package com.evervault.services;

import com.evervault.contracts.IProvideCageExecution;
import com.evervault.contracts.IProvideCagePublicKeyFromHttpApi;
import com.evervault.contracts.IProvideOutboundRelayConfigFromHttpApi;
import com.evervault.contracts.IProvideRunToken;
import com.evervault.exceptions.HttpFailureException;
import com.evervault.models.CagePublicKey;
import com.evervault.models.CageRunResult;
import com.evervault.models.OutboundRelayConfigResult;
import com.evervault.models.RunTokenResult;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpHandler implements IProvideCagePublicKeyFromHttpApi, IProvideCageExecution, IProvideRunToken, IProvideOutboundRelayConfigFromHttpApi {

    private final java.net.http.HttpClient client;
    private final static String VERSION_PREFIX = "evervault-java/";
    private final static String JSON_CONTENT_TYPE = "application/json";
    private final static int OK_HTTP_STATUS_CODE = 200;
    private final static String POLL_INTERVAL_HEADER_NAME = "X-Poll-Interval";
    private final static String ASYNC_HEADER_NAME = "x-async";
    private final static String VERSION_ID_HEADER_NAME = "x-version-id";
    private final static long TIMEOUT_SECONDS_DEFAULT = 30;
    private final static String CAGES_KEY_SUFFIX = "/cages/key";
    private final String apiKey;
    private final Duration httpTimeout;

    public HttpHandler(String apiKey) {
        this(apiKey, Duration.ofSeconds(TIMEOUT_SECONDS_DEFAULT));
    }

    public HttpHandler(String apiKey, Duration httpTimeout) {
        this.apiKey = apiKey;
        this.httpTimeout = httpTimeout;
        client = java.net.http.HttpClient.newHttpClient();
    }

    public CagePublicKey getCagePublicKeyFromEndpoint(String url) throws IOException, InterruptedException, HttpFailureException {
        return this.getCagePublicKeyFromEndpoint(url, null);
    }

    public CagePublicKey getCagePublicKeyFromEndpoint(String url, Map<String, String> headerMap) throws IOException, InterruptedException, HttpFailureException {
        var uri = URI.create(url);
        var finalAddress = uri.resolve(CAGES_KEY_SUFFIX);

        var requestBuilder = HttpRequest.newBuilder()
                .uri(finalAddress)
                .timeout(httpTimeout)
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("AcceptEncoding", "gzip, deflate")
                .setHeader("Accept", JSON_CONTENT_TYPE)
                .setHeader("Content-Type", JSON_CONTENT_TYPE)
                .setHeader("Api-Key", apiKey)
                .GET();

        if (headerMap != null) {
            for (HashMap.Entry<String, String> set :
                    headerMap.entrySet()) {
                requestBuilder.setHeader(set.getKey(), set.getValue());
            }
        }

        var request = requestBuilder.build();

        var result = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (result.statusCode() != OK_HTTP_STATUS_CODE) {
            throw new HttpFailureException(result.statusCode(), result.body());
        }

        return new Gson().fromJson(result.body(), CagePublicKey.class);
    }

    @Override
    public CageRunResult runCage(String url, String cageName, Object data, boolean async, String version) throws HttpFailureException, IOException, InterruptedException {
        var serializedData = new Gson().toJson(data);

        var uri = URI.create(url);
        var finalAddress = uri.resolve("/" + cageName);

        var requestBuilder = HttpRequest.newBuilder()
                .uri(finalAddress)
                .setHeader("Api-Key", apiKey)
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("Accept", JSON_CONTENT_TYPE)
                .setHeader("Content-Type", JSON_CONTENT_TYPE)
                .timeout(httpTimeout)
                .POST(BodyPublishers.ofString(serializedData));

        if (async) {
            requestBuilder.setHeader(ASYNC_HEADER_NAME, "true");
        }

        if (version != null && !version.isEmpty()) {
            requestBuilder.setHeader(VERSION_ID_HEADER_NAME, version);
        }

        var request = requestBuilder.build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != OK_HTTP_STATUS_CODE) {
            throw new HttpFailureException(response.statusCode(), response.body());
        }

        return new Gson().fromJson(response.body(), CageRunResult.class);
    }

    @Override
    public RunTokenResult createRunToken(String url, String cageName, Object data) throws HttpFailureException, IOException, InterruptedException {
        var serializedData = new Gson().toJson(data);

        var uri = URI.create(url);
        var finalAddress = uri.resolve("/v2/functions/" + cageName + "/run-token");

        var requestBuilder = HttpRequest.newBuilder()
                .uri(finalAddress)
                .setHeader("Api-Key", apiKey)
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("Accept", JSON_CONTENT_TYPE)
                .setHeader("Content-Type", JSON_CONTENT_TYPE)
                .timeout(httpTimeout)
                .POST(BodyPublishers.ofString(serializedData));

        var request = requestBuilder.build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != OK_HTTP_STATUS_CODE) {
            throw new HttpFailureException(response.statusCode(), response.body());
        }

        return new Gson().fromJson(response.body(), RunTokenResult.class);
    }

    @Override
    public RunTokenResult createRunToken(String url, String cageName) throws HttpFailureException, IOException, InterruptedException {
        // Allow non pre-approved payloads for run tokens
        // If data is null, convert to an empty object
        var serializedData = new Gson().toJson(new Object());

        var uri = URI.create(url);
        var finalAddress = uri.resolve("/v2/functions/" + cageName + "/run-token");

        var requestBuilder = HttpRequest.newBuilder()
                .uri(finalAddress)
                .setHeader("Api-Key", apiKey)
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("Accept", JSON_CONTENT_TYPE)
                .setHeader("Content-Type", JSON_CONTENT_TYPE)
                .timeout(httpTimeout)
                .POST(BodyPublishers.ofString(serializedData));

        var request = requestBuilder.build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != OK_HTTP_STATUS_CODE) {
            throw new HttpFailureException(response.statusCode(), response.body());
        }

        return new Gson().fromJson(response.body(), RunTokenResult.class);
    }

    public OutboundRelayConfigResult getOutboundRelayConfig(String url) throws HttpFailureException, IOException, InterruptedException {
        var uri = URI.create(url);
        var finalAddress = uri.resolve("/v2/relay-outbound");

        var requestBuilder = HttpRequest.newBuilder()
                .uri(finalAddress)
                .setHeader("Api-Key", apiKey)
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("Accept", JSON_CONTENT_TYPE)
                .setHeader("AcceptEncoding", "gzip, deflate")
                .timeout(httpTimeout)
                .GET();

        var request = requestBuilder.build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != OK_HTTP_STATUS_CODE) {
            throw new HttpFailureException(response.statusCode(), response.body());
        }

        var config = new Gson().fromJson(response.body(), OutboundRelayConfigResult.OutboundRelayConfig.class);
        var pollInterval = response.headers().firstValue(POLL_INTERVAL_HEADER_NAME).flatMap(s -> {
            try {
                return Optional.of(Integer.valueOf(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }).orElse(null);
        return new OutboundRelayConfigResult(pollInterval, config);
    }
}
