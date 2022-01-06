package EverVault;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@WireMockTest
public class WhenPerformingHttpRequestTests {

    @Test
    public void httpHeadersAreIncludedForGets(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException, InterruptedException {
        final String endpoint = "/Foo";
        final String userAgentHeaderRegex = "evervault-java/1.0";
        final String contentType = "application/json";
        final String apiKey = "Foo";

        var client = new HttpHandler(apiKey);

        final var urlPath = wireMockRuntimeInfo.getHttpBaseUrl() + endpoint;

        client.get(urlPath);

        verify(getRequestedFor(urlEqualTo(endpoint))
                .withHeader("User-Agent", equalTo(userAgentHeaderRegex))
                .withHeader("AcceptEncoding", equalTo("gzip, deflate"))
                .withHeader("Accept", equalTo(contentType))
                .withHeader("Content-Type", equalTo(contentType))
                .withHeader("Api-Key", equalTo(apiKey)));
    }
}
