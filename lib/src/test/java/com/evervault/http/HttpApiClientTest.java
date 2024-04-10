package com.evervault.http;

import com.evervault.EvervaultCredentials;
import com.evervault.http.exceptions.ApiErrorException;
import com.evervault.http.requests.CreateClientSideTokenRequest;
import com.evervault.http.requests.CreateFunctionRunRequest;
import com.evervault.http.responses.CreateClientSideTokenResponse;
import com.evervault.http.responses.GetAppKeysResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HttpApiClientTest {

    @Test
    public void getAppKeys_returnsListOfKeys_IfCredentialsAreValid() throws IOException, ApiErrorException {
        // Given
        EvervaultCredentials ec = new EvervaultCredentials("app_28807f2a6bb1", "ev:key:1:5L14NDMqw5AHEi1ClVvXHDvhm6Q2pyyHCnirTFGZAwcqz2P740UN4QdnGQsoKOcgB:sZ4zvj:Qqbh2V");

        // When
        HttpApiClient client = new HttpApiClient(ec);
        GetAppKeysResponse actual = client.getAppKeys();

        Class type = EvervaultCredentials.class;
        // Then

    }

    @Test
    public void test() {
        try {
            EvervaultCredentials ec = new EvervaultCredentials("app_28807f2a6bb1", "test");
            HttpApiClient client = new HttpApiClient(ec);
//            GetAppKeysResponse response = client.getAppKeysResponse();
//            System.out.println(response);

            CreateClientSideTokenResponse response1 = client.createClientSideToken(new CreateClientSideTokenRequest("api:decrypt", null, "hello world!", null));
            System.out.println(response1);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
