package EverVault;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpHandler {

    private final java.net.http.HttpClient client;
    private final String VERSION_PREFIX = "evervault-java/";
    private final String CONTENT_TYPE = "application/json";
    private final String apiKey;

    public HttpHandler(String apiKey) {
        this.apiKey = apiKey;
        client = java.net.http.HttpClient.newHttpClient();
    }

    public void get(String url) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(10))
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .setHeader("AcceptEncoding", "gzip, deflate")
                .setHeader("Accept", CONTENT_TYPE)
                .setHeader("Content-Type", CONTENT_TYPE)
                .setHeader("Api-Key", apiKey)
                .GET()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
