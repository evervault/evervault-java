package com.evervault.services;

import com.evervault.contracts.*;
import com.evervault.models.*;
import com.evervault.utils.Base64Handler;
import com.evervault.exceptions.HttpFailureException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class HttpHandler implements IProvideFunctionRun, IProvideCagePublicKeyFromHttpApi, IProvideCageExecution, IProvideRunToken, IProvideOutboundRelayConfigFromHttpApi, IProvideDecrypt, IProvideClientSideToken {
    private final static String VERSION_PREFIX = "evervault-java/";
    private final static String JSON_CONTENT_TYPE = "application/json";

    private final static String POLL_INTERVAL_HEADER_NAME = "X-Poll-Interval";
    private final static String ASYNC_HEADER_NAME = "x-async";
    private final static String VERSION_ID_HEADER_NAME = "x-version-id";
    private final static int TIMEOUT_MILLIS_DEFAULT = 30000;
    private final static String CAGES_KEY_SUFFIX = "/cages/key";
    private final String apiKey;
    private final String appUuid;
    private final String basicAuthorizationHeaderValue;
    private final int httpTimeout;

    public HttpHandler(String apiKey, String appUuid) {
        this(apiKey, appUuid, TIMEOUT_MILLIS_DEFAULT);
    }

    public HttpHandler(String apiKey, String appUuid, int httpTimeout) {
        this.apiKey = apiKey;
        this.appUuid = appUuid;
        this.basicAuthorizationHeaderValue = buildAuthorizationHeaderValue(appUuid, apiKey);
        this.httpTimeout = httpTimeout;
    }

    public CagePublicKey getCagePublicKeyFromEndpoint(String url) throws IOException, InterruptedException, HttpFailureException {
        return this.getCagePublicKeyFromEndpoint(url, null);
    }

    public CagePublicKey getCagePublicKeyFromEndpoint(String url, Map<String, String> headerMap) throws IOException, InterruptedException, HttpFailureException {
        URL connectionUrl = new URL(url + CAGES_KEY_SUFFIX);
        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Api-Key", apiKey);
        if (headerMap != null) {
            headerMap.forEach((key, value) -> additionalHeaders.put(key, value));
        }
        HttpURLConnection connection = createConnection(connectionUrl, "GET", additionalHeaders, httpTimeout);
        sendRequest(connection);
        return parseResponseBody(connection, CagePublicKey.class);
    }

    @Override
    public CageRunResult runCage(String url, String cageName, Object data, boolean async, String version) throws HttpFailureException, IOException {
        String serializedData = new Gson().toJson(data);

        URL connectionUrl = new URL(url + "/" + cageName);
        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Api-Key", apiKey);
        if (async) {
            additionalHeaders.put(ASYNC_HEADER_NAME, "true");
        }
        if (version != null && !version.isEmpty()) {
            additionalHeaders.put(VERSION_ID_HEADER_NAME, version);
        }

        HttpURLConnection connection = createConnection(connectionUrl, "POST", additionalHeaders, httpTimeout);
        setRequestBody(connection, serializedData);
        sendRequest(connection);

        return parseResponseBody(connection, CageRunResult.class);
    }

    public <T> FunctionRun<T> runFunction(String url, String functionName, Object payload, Class<T> responseType, boolean async, int timeout) throws HttpFailureException, IOException {
        CreateFunctionRunRequest request = new CreateFunctionRunRequest(payload, async);
        String serializedData = new Gson().toJson(request);

        URL connectionUrl = new URL(url + "/functions/" + functionName + "/runs");

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Authorization", basicAuthorizationHeaderValue);

        HttpURLConnection connection = createConnection(connectionUrl, "POST", additionalHeaders, timeout);
        setRequestBody(connection, serializedData);
        sendRequest(connection);

        Type functionRunType = TypeToken.getParameterized(FunctionRun.class, responseType).getType();
        return parseResponseBody(connection, functionRunType);
    }

    @Override
    public <T> T decrypt(String url, Object data, Class<T> valueType) throws HttpFailureException, IOException {
        String serializedData = new Gson().toJson(data);

        URL connectionUrl = new URL(url + "/decrypt");

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Authorization", basicAuthorizationHeaderValue);

        HttpURLConnection connection = createConnection(connectionUrl, "POST", additionalHeaders, httpTimeout);
        setRequestBody(connection, serializedData);
        sendRequest(connection);

        return parseResponseBody(connection, valueType);
    }

    @Override
    public TokenResult createClientSideToken(String url, String action, Object data, Instant expiry) throws HttpFailureException, IOException {
        Long expiryInMillis;
        if (expiry != null) {
            expiryInMillis = expiry.toEpochMilli();
        } else {
            expiryInMillis = null;
        }

        CreateTokenPayload payload = new CreateTokenPayload(action, expiryInMillis, data);
        String serializedData = new Gson().toJson(payload);

        URL connectionUrl = new URL(url + "/client-side-tokens");

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Authorization", basicAuthorizationHeaderValue);

        HttpURLConnection connection = createConnection(connectionUrl, "POST", additionalHeaders, httpTimeout);
        setRequestBody(connection, serializedData);
        sendRequest(connection);

        return parseResponseBody(connection, TokenResult.class);
    }

    @Override
    public TokenResult createClientSideToken(String url, String action, Object data) throws HttpFailureException, IOException {
        return createClientSideToken(url, action, data, null);
    }

    @Override
    public RunTokenResult createRunToken(String url, String cageName, Object data) throws HttpFailureException, IOException {
        String serializedData = new Gson().toJson(data);
        URL connectionUrl = new URL(url + "/v2/functions/" + cageName + "/run-token");

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Api-key", apiKey);

        HttpURLConnection connection = createConnection(
                connectionUrl,
                "POST",
                additionalHeaders,
                httpTimeout
        );
        setRequestBody(connection, serializedData);
        sendRequest(connection);

        return parseResponseBody(connection, RunTokenResult.class);
    }

    @Override
    public RunTokenResult createRunToken(String url, String cageName) throws HttpFailureException, IOException {
        // Allow non pre-approved payloads for run tokens
        // If data is null, convert to an empty object
        return createRunToken(url, cageName, new Object());
    }

    public OutboundRelayConfigResult getOutboundRelayConfig(String url) throws HttpFailureException, IOException {
        URL connectionUrl = new URL(url + "/v2/relay-outbound");
        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("Api-key", apiKey);
        HttpURLConnection connection = createConnection(
                connectionUrl,
                "GET",
                additionalHeaders,
                httpTimeout
        );

        sendRequest(connection);

        OutboundRelayConfigResult.OutboundRelayConfig config =
                parseResponseBody(connection, OutboundRelayConfigResult.OutboundRelayConfig.class);
        String pollIntervalHeaderValue = connection.getHeaderField(POLL_INTERVAL_HEADER_NAME);
        Integer pollInterval;
        try {
            pollInterval = Integer.valueOf(pollIntervalHeaderValue);
        } catch (NumberFormatException e) {
            pollInterval = null;
        }
        return new OutboundRelayConfigResult(pollInterval, config);
    }

    private String buildAuthorizationHeaderValue(String appUuid, String apiKey) {
        String input = appUuid + ":" + apiKey;
        String encodedValue = Base64Handler.encodeBase64(input.getBytes());
        StringBuilder builder = new StringBuilder();
        builder.append("Basic ")
                .append(encodedValue);
        return builder.toString();
    }

    private HttpURLConnection createConnection(URL url, String method, Map<String, String> headers, int timeout) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if(headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        connection.setRequestMethod(method);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.setRequestProperty("User-Agent", VERSION_PREFIX + 1.0);
        connection.setRequestProperty("Accept", JSON_CONTENT_TYPE);
        return connection;
    }

    private void setRequestBody(HttpURLConnection connection, String body) throws IOException {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", JSON_CONTENT_TYPE);
        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    }

    private void sendRequest(HttpURLConnection connection) throws HttpFailureException, IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            StringBuilder response = new StringBuilder();
            InputStream is = connection.getErrorStream();
            if (is != null) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                        response.append('\n');
                    }
                }
            }
            throw new HttpFailureException(responseCode, response.toString());
        }
    }

    private <T> T parseResponseBody(HttpURLConnection connection, Class<T> clazz) throws IOException {
        T parsed;
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            parsed = new Gson().fromJson(reader, clazz);
        }
        return parsed;
    }

    private <T> T parseResponseBody(HttpURLConnection connection, Type type) throws IOException {
        T parsed;
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            parsed = new Gson().fromJson(reader, type);
        }
        return parsed;
    }
}
