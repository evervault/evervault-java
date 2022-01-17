package EverVault.Services;

import EverVault.Contracts.IProvideCageExecution;
import EverVault.Contracts.IProvideCagePublicKeyFromHttpApi;
import EverVault.Exceptions.HttpFailureException;
import EverVault.ReadModels.CagePublicKey;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class HttpApiRepository implements IProvideCagePublicKeyFromHttpApi, IProvideCageExecution {

    private final java.net.http.HttpClient client;
    private final static String VERSION_PREFIX = "evervault-java/";
    private final static String CONTENT_TYPE = "application/json";
    private final static int OK_HTTP_STATUS_CODE = 200;
    private final String apiKey;

    public HttpApiRepository(String apiKey) {
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
    public void runCage(String cageName, Serializable data, boolean async, String version) throws HttpFailureException {
        throw new HttpFailureException(550);
    }
}