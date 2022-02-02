package evervault.contracts;

import evervault.exceptions.HttpFailureException;
import evervault.models.CagePublicKey;

import java.io.IOException;
import java.util.Map;

public interface IProvideCagePublicKeyFromHttpApi {
    CagePublicKey getCagePublicKeyFromEndpoint(String url) throws IOException, InterruptedException, HttpFailureException;
    CagePublicKey getCagePublicKeyFromEndpoint(String url, Map<String, String> headerMap) throws IOException, InterruptedException, HttpFailureException;
}
