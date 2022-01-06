package EverVault;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@WireMockTest
public class WhenPerformingHttpRequestTests {

    @Test
    public void httpHeadersAreIncludedForGets(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException, InterruptedException {
        final String endpoint = "/Foo";
        final String userAgentHeaderRegex = "evervault-java/1.0";

        var client = new HttpHandler();

        var urlPath = wireMockRuntimeInfo.getHttpBaseUrl() + endpoint;

        client.get(URI.create(urlPath));

        verify(getRequestedFor(urlEqualTo(endpoint))
                .withHeader("User-Agent", equalTo(userAgentHeaderRegex)));
    }
}
