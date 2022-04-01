package com.evervault.contracts;

import com.evervault.models.CagePublicKey;
import com.evervault.exceptions.HttpFailureException;

import java.io.IOException;
import java.util.Map;

public interface IProvideCagePublicKeyFromHttpApi {
    CagePublicKey getCagePublicKeyFromEndpoint(String url) throws IOException, InterruptedException, HttpFailureException;
    CagePublicKey getCagePublicKeyFromEndpoint(String url, Map<String, String> headerMap) throws IOException, InterruptedException, HttpFailureException;
}
