package com.evervault.http;

import com.evervault.EvervaultCredentials;
import com.evervault.http.exceptions.ApiErrorException;
import com.evervault.http.requests.CreateClientSideTokenRequest;
import com.evervault.http.requests.CreateFunctionRunRequest;
import com.evervault.http.responses.CreateClientSideTokenResponse;
import com.evervault.http.responses.CreateFunctionRunResponse;
import com.evervault.http.responses.GetAppKeysResponse;
import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HttpApiClient implements ApiClient {

    private static final String API_URL = "https://api.evervault.com";

    private final EvervaultCredentials credentials;

    private final Gson gson;

    public HttpApiClient(EvervaultCredentials credentials) {
        if (credentials == null) {
            throw new IllegalArgumentException("`credentials` can't be null");
        }
        this.credentials = credentials;
        this.gson = new Gson();
    }

    public GetAppKeysResponse getAppKeys() throws IOException, ApiErrorException {
        URL url = new URL(API_URL + "/cages/key");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Api-Key", credentials.getApiKey());
        return handleResponse(connection, GetAppKeysResponse.class);
    }

    public <T> T decrypt(Object encrypted, Class<T> type) throws IOException, ApiErrorException {
        URL url = new URL(API_URL + "/decrypt");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", createAuthorisationHeader());
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return null;
    }

    public CreateFunctionRunResponse createFunctionRun(String functionName, CreateFunctionRunRequest request) throws IOException, ApiErrorException {
        URL url = new URL(API_URL + "/functions/function_name/runs");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        //handleResponse(connection,CreateFunctionRunResponse<>);

        return null;
    }

    public CreateClientSideTokenResponse createClientSideToken(CreateClientSideTokenRequest request) throws IOException, ApiErrorException {
        URL url = new URL(API_URL + "/client-side-tokens");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", createAuthorisationHeader());
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        gson.toJson(request, new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
        return handleResponse(connection, CreateClientSideTokenResponse.class);
    }

    private <T> T handleResponse(HttpsURLConnection connection, Class<T> clazz) throws IOException, ApiErrorException {
        int responseStatusCode = connection.getResponseCode();
        if (responseStatusCode >= 200 && responseStatusCode < 300) {
            return gson.fromJson(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8), clazz);
        } else {
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                throw gson.fromJson(new InputStreamReader(errorStream, StandardCharsets.UTF_8), ApiErrorException.class);
            } else {
                throw new ApiErrorException("internal-server-error", "Internal Server Error", 500, "An internal error occurred. For additional assistance, please contact support@evervault.com.");
            }
        }
    }

    private String createAuthorisationHeader() {
        String formattedCredentials = credentials.getAppId() + ":" + credentials.getApiKey();
        String encodedCredentials = new String(Base64.getEncoder().encode(formattedCredentials.getBytes(StandardCharsets.UTF_8)));
        return "Basic " + encodedCredentials;
    }

    private String test() {
        InetAddress
        return "";
    }

}
