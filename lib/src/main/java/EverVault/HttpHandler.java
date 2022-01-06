package EverVault;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpHandler {

    private final java.net.http.HttpClient client;
    private final String VERSION_PREFIX = "evervault-java/";

    public HttpHandler() {
        client = java.net.http.HttpClient.newHttpClient();
    }

    public void get(URI url) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.ofMinutes(10))
                .setHeader("User-Agent", VERSION_PREFIX + 1.0)
                .GET()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
