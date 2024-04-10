package com.evervault.http;

import com.evervault.EvervaultCredentials;
import org.junit.jupiter.api.Test;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RelaySslSocketFactoryTest {

    @Test
    public void test() throws IOException {
        EvervaultCredentials ec = new EvervaultCredentials("app_28807f2a6bb1", "ev:key:1:71yMXsxxG6nPeSyIjuTDLuh0ci9I3OFeqq7JycfEQjPBTwBtF4zbcvRC21kqOyJ5v:sZ4zvj:Hx+cV/");
        SSLSocketFactory sf = new RelaySslSocketFactory(ec);

        URL url = new URL("https://blackhole.posterior.io/rxn2jm");
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setSSLSocketFactory(sf);

        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        urlConnection.setRequestProperty("Accept", "application/json");

        String jsonInputString = "{\"message\": \"lol\"}";

        try(OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

//        int statusCode = urlConnection.getResponseCode();
//        System.out.println("HTTP Status Code: " + statusCode);
//        try(BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))) {
//            StringBuilder response = new StringBuilder();
//            String responseLine;
//            while ((responseLine = br.readLine()) != null) {
//                response.append(responseLine.trim());
//            }
//            System.out.println("Server response: " + response.toString());
//        }

        urlConnection.disconnect();
    }

}
