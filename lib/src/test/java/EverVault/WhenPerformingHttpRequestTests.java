package EverVault;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@WireMockTest
public class WhenPerformingHttpRequestTests {

    @Test
    public void httpHeadersAreIncludedForGets(WireMockRuntimeInfo wireMockRuntimeInfo) {
        final String endpoint = "/Foo";
        final String userAgentHeaderRegex = "evervault-java\\/([0-9]\\.*)+";
        var wireMock = wireMockRuntimeInfo.getWireMock();
        wireMock.register(get("/Foo")
                        .withHeader("UserAgent", matching(userAgentHeaderRegex))
                .willReturn(ok()));

        var urlPath = wireMockRuntimeInfo.getHttpBaseUrl() + endpoint;

        var client = new HttpHandler();

        client.get(URI.create(urlPath));
    }
}
