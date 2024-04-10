package com.evervault.services;

import com.evervault.utils.Base64Handler;
import com.evervault.contracts.IProvideCageExecution;
import com.evervault.contracts.IProvideCagePublicKeyFromHttpApi;
import com.evervault.contracts.IProvideClientSideToken;
import com.evervault.contracts.IProvideDecrypt;
import com.evervault.contracts.IProvideOutboundRelayConfigFromHttpApi;
import com.evervault.contracts.IProvideRunToken;
import com.evervault.exceptions.HttpFailureException;
import com.evervault.models.CagePublicKey;
import com.evervault.models.CageRunResult;
import com.evervault.models.CreateTokenPayload;
import com.evervault.models.OutboundRelayConfigResult;
import com.evervault.models.RunTokenResult;
import com.evervault.models.TokenResult;
import com.google.gson.Gson;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpHandler implements IProvideCagePublicKeyFromHttpApi, IProvideCageExecution, IProvideRunToken, IProvideOutboundRelayConfigFromHttpApi, IProvideDecrypt, IProvideClientSideToken {

    private final java.net.http.HttpClient client;
    private final static String VERSION_PREFIX = "evervault-java/";
    private final static String JSON_CONTENT_TYPE = "application/json";
    private final static int OK_HTTP_STATUS_CODE = 200;
    private final static int OK_CREATED_HTTP_STATUS_CODE = 201;
    private final static String POLL_INTERVAL_HEADER_NAME = "X-Poll-Interval";
    private final static String ASYNC_HEADER_NAME = "x-async";
    private final static String VERSION_ID_HEADER_NAME = "x-version-id";
    private final static long TIMEOUT_SECONDS_DEFAULT = 30;
    private final static String CAGES_KEY_SUFFIX = "/cages/key";
    private final String apiKey;
    private final String appUuid;
    private final Duration httpTimeout;

    public HttpHandler(String apiKey, String appUuid) {
        this(apiKey, appUuid, Duration.ofSeconds(TIMEOUT_SECONDS_DEFAULT));
    }

    public HttpHandler(String apiKey, String appUuid, Duration httpTimeout) {
        this.apiKey = apiKey;
        this.appUuid = appUuid;
        this.httpTimeout = httpTimeout;
        client = java.net.http.HttpClient.newHttpClient();
    }

    public CagePublicKey getCagePublicKeyFromEndpoint(String url) throws IOException, InterruptedException, HttpFailureException {
        return this.getCagePublicKeyFromEndpoint(url, null);
    }

    private String buildAuthorizationHeaderValue() {
        String input = appUuid + ":" + apiKey;
        String encodedValue = Base64Handler.encodeBase64(input.getBytes());
        StringBuilder builder = new StringBuilder();
        builder.append("Basic ")
            .append(encodedValue);
        return builder.toString();
    }

    public CagePublicKey getCagePublicKeyFromEndpoint(String url, Map<String, String> headerMap) throws IOException, InterruptedException, HttpFailureException {
        URI uri = URI.create(url);
        URI finalAddress = uri.resolve(CAGES_KEY_SUFFIX);

        String authHeaderValue = this.buildAuthorizationHeaderValue();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(finalAddress)
                .timeout(httpTimeout)
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("AcceptEncoding", "gzip, deflate")
                .setHeader("Accept", JSON_CONTENT_TYPE)
                .setHeader("Content-Type", JSON_CONTENT_TYPE)
                .setHeader("Api-Key", apiKey)
                .setHeader("Authorization", authHeaderValue)
                .GET();

        if (headerMap != null) {
            for (HashMap.Entry<String, String> set :
                    headerMap.entrySet()) {
                requestBuilder.setHeader(set.getKey(), set.getValue());
            }
        }

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> result = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (result.statusCode() != OK_HTTP_STATUS_CODE) {
            throw new HttpFailureException(result.statusCode(), result.body());
        }

        return new Gson().fromJson(result.body(), CagePublicKey.class);
    }

    @Override
    public CageRunResult runCage(String url, String cageName, Object data, boolean async, String version) throws HttpFailureException, IOException, InterruptedException {
        String serializedData = new Gson().toJson(data);

        URI uri = URI.create(url);
        URI finalAddress = uri.resolve("/" + cageName);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(finalAddress)
                .setHeader("Api-Key", apiKey)
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("Accept", JSON_CONTENT_TYPE)
                .setHeader("Content-Type", JSON_CONTENT_TYPE)
                .setHeader("Api-Key", apiKey)
                .timeout(httpTimeout)
                .POST(BodyPublishers.ofString(serializedData));

        if (async) {
            requestBuilder.setHeader(ASYNC_HEADER_NAME, "true");
        }

        if (version != null && !version.isEmpty()) {
            requestBuilder.setHeader(VERSION_ID_HEADER_NAME, version);
        }

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != OK_HTTP_STATUS_CODE) {
            throw new HttpFailureException(response.statusCode(), response.body());
        }

        return new Gson().fromJson(response.body(), CageRunResult.class);
    }

    @Override
    public <T> T decrypt(String url, Object data, Class<T> valueType) throws HttpFailureException, IOException, InterruptedException {
        String serializedData = new Gson().toJson(data);

        URI uri = URI.create(url);
        URI finalAddress = uri.resolve("/decrypt");

        String authHeaderValue = this.buildAuthorizationHeaderValue();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(finalAddress)
            .setHeader("User-Agent", VERSION_PREFIX + 1.0)
            .setHeader("Accept", JSON_CONTENT_TYPE)
            .setHeader("Content-Type", JSON_CONTENT_TYPE)
            .setHeader("Authorization", authHeaderValue)
            .timeout(httpTimeout)
            .POST(BodyPublishers.ofString(serializedData));
        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != OK_HTTP_STATUS_CODE){
            throw new HttpFailureException(response.statusCode(), response.body());
        }

        return new Gson().fromJson(response.body(), valueType);
    }

    @Override
    public TokenResult createClientSideToken(String url, String action, Object data, Instant expiry) throws HttpFailureException, IOException, InterruptedException {
        long expiryInMillis = expiry.toEpochMilli();

        CreateTokenPayload payload = new CreateTokenPayload(action, expiryInMillis, data);
        String serializedData = new Gson().toJson(payload);

        URI uri = URI.create(url);
        URI finalAddress = uri.resolve("/client-side-tokens");

        String authHeaderValue = this.buildAuthorizationHeaderValue();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(finalAddress)
            .setHeader("User-Agent", VERSION_PREFIX + 1.0)
            .setHeader("Accept", JSON_CONTENT_TYPE)
            .setHeader("Content-Type", JSON_CONTENT_TYPE)
            .setHeader("Authorization", authHeaderValue)
            .timeout(httpTimeout)
            .POST(BodyPublishers.ofString(serializedData));
        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 && response.statusCode() >= 300) {
            throw new HttpFailureException(response.statusCode(), response.body());
        }

        return new Gson().fromJson(response.body(), TokenResult.class);
    }

    @Override
    public TokenResult createClientSideToken(String url, String action, Object data) throws HttpFailureException, IOException, InterruptedException {
        CreateTokenPayload payload = new CreateTokenPayload(action, data);
        String serializedData = new Gson().toJson(payload);

        URI uri = URI.create(url);
        URI finalAddress = uri.resolve("/client-side-tokens");

        String authHeaderValue = this.buildAuthorizationHeaderValue();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(finalAddress)
            .setHeader("User-Agent", VERSION_PREFIX + 1.0)
            .setHeader("Accept", JSON_CONTENT_TYPE)
            .setHeader("Content-Type", JSON_CONTENT_TYPE)
            .setHeader("Authorization", authHeaderValue)
            .timeout(httpTimeout)
            .POST(BodyPublishers.ofString(serializedData));
        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != OK_CREATED_HTTP_STATUS_CODE) {
            throw new HttpFailureException(response.statusCode(), response.body());
        }

        return new Gson().fromJson(response.body(), TokenResult.class);
    }

    @Override
    public RunTokenResult createRunToken(String url, String cageName, Object data) throws HttpFailureException, IOException, InterruptedException {
        String serializedData = new Gson().toJson(data);

        URI uri = URI.create(url);
        URI finalAddress = uri.resolve("/v2/functions/" + cageName + "/run-token");

        String authHeaderValue = this.buildAuthorizationHeaderValue();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(finalAddress)
                .setHeader("Api-Key", apiKey)
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("Accept", JSON_CONTENT_TYPE)
                .setHeader("Content-Type", JSON_CONTENT_TYPE)
                .setHeader("Api-Key", apiKey)
                .timeout(httpTimeout)
                .POST(BodyPublishers.ofString(serializedData));

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != OK_HTTP_STATUS_CODE) {
            throw new HttpFailureException(response.statusCode(), response.body());
        }

        return new Gson().fromJson(response.body(), RunTokenResult.class);
    }

    @Override
    public RunTokenResult createRunToken(String url, String cageName) throws HttpFailureException, IOException, InterruptedException {
        // Allow non pre-approved payloads for run tokens
        // If data is null, convert to an empty object
        String serializedData = new Gson().toJson(new Object());

        URI uri = URI.create(url);
        URI finalAddress = uri.resolve("/v2/functions/" + cageName + "/run-token");

        String authHeaderValue = this.buildAuthorizationHeaderValue();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(finalAddress)
                .setHeader("Api-Key", apiKey)
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("Accept", JSON_CONTENT_TYPE)
                .setHeader("Content-Type", JSON_CONTENT_TYPE)
                .setHeader("Api-Key", apiKey)
                .timeout(httpTimeout)
                .POST(BodyPublishers.ofString(serializedData));

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != OK_HTTP_STATUS_CODE) {
            throw new HttpFailureException(response.statusCode(), response.body());
        }

        return new Gson().fromJson(response.body(), RunTokenResult.class);
    }

    public OutboundRelayConfigResult getOutboundRelayConfig(String url) throws HttpFailureException, IOException, InterruptedException {
        URI uri = URI.create(url);
        URI finalAddress = uri.resolve("/v2/relay-outbound");

        String authHeaderValue = this.buildAuthorizationHeaderValue();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(finalAddress)
                .setHeader("Api-Key", apiKey)
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("Accept", JSON_CONTENT_TYPE)
                .setHeader("AcceptEncoding", "gzip, deflate")
                .setHeader("Api-Key", apiKey)
                .timeout(httpTimeout)
                .GET();

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != OK_HTTP_STATUS_CODE) {
            throw new HttpFailureException(response.statusCode(), response.body());
        }

        OutboundRelayConfigResult.OutboundRelayConfig config =
                new Gson().fromJson(response.body(), OutboundRelayConfigResult.OutboundRelayConfig.class);
        Integer pollInterval = response.headers().firstValue(POLL_INTERVAL_HEADER_NAME).flatMap(s -> {
            try {
                return Optional.of(Integer.valueOf(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }).orElse(null);
        return new OutboundRelayConfigResult(pollInterval, config);
    }
}
