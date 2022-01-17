package EverVault.Services;

import EverVault.Contracts.IProvideCageExecution;
import EverVault.Contracts.IProvideCagePublicKeyFromHttpApi;
import EverVault.Exceptions.HttpFailureException;
import EverVault.ReadModels.CagePublicKey;
import EverVault.ReadModels.CageRunResult;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class HttpHandler implements IProvideCagePublicKeyFromHttpApi, IProvideCageExecution {

    private final java.net.http.HttpClient client;
    private final static String VERSION_PREFIX = "evervault-java/";
    private final static String CONTENT_TYPE = "application/json";
    private final static int OK_HTTP_STATUS_CODE = 200;
    private final static String HEADER_FOR_ASYNC_FIELD = "x-async";
    private final static String HEADER_FOR_VERSION_FIELD = "x-version-id";
    private final String apiKey;

    public HttpHandler(String apiKey) {
        this.apiKey = apiKey;
        client = java.net.http.HttpClient.newHttpClient();
    }

    public CagePublicKey getCagePublicKeyFromEndpoint(String url) throws IOException, InterruptedException, HttpFailureException {
        return this.getCagePublicKeyFromEndpoint(url, null);
    }

    public CagePublicKey getCagePublicKeyFromEndpoint(String url, Map<String, String> headerMap) throws IOException, InterruptedException, HttpFailureException {
        var requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(10))
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("AcceptEncoding", "gzip, deflate")
                .setHeader("Accept", CONTENT_TYPE)
                .setHeader("Content-Type", CONTENT_TYPE)
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
            throw new HttpFailureException(result.statusCode());
        }

        return new Gson().fromJson(result.body(), CagePublicKey.class);
    }

    @Override
    public CageRunResult runCage(String url, String cageName, Object data, boolean async, String version) throws HttpFailureException, IOException, InterruptedException {
        var serializedData = new Gson().toJson(data);

        var requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + cageName))
                .timeout(Duration.ofMinutes(10))
                .POST(BodyPublishers.ofString(serializedData));

        if (async) {
            requestBuilder.setHeader(HEADER_FOR_ASYNC_FIELD, "true");
        }

        if (version != null && !version.isEmpty()) {
            requestBuilder.setHeader(HEADER_FOR_VERSION_FIELD, version);
        }

        var request = requestBuilder.build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != OK_HTTP_STATUS_CODE) {
            throw new HttpFailureException(response.statusCode());
        }

        return new Gson().fromJson(response.body(), CageRunResult.class);
    }
}
