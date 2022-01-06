package EverVault;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpHandler {

    private final java.net.http.HttpClient client;

    public HttpHandler() {
        client = java.net.http.HttpClient.newHttpClient();
    }

    public void get(URI url) {
        var request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.ofMinutes(10))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
