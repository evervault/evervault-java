package evervault.Contracts;

import evervault.Exceptions.HttpFailureException;
import evervault.ReadModels.CagePublicKey;

import java.io.IOException;
import java.util.Map;

public interface IProvideCagePublicKeyFromHttpApi {
    CagePublicKey getCagePublicKeyFromEndpoint(String url) throws IOException, InterruptedException, HttpFailureException;
    CagePublicKey getCagePublicKeyFromEndpoint(String url, Map<String, String> headerMap) throws IOException, InterruptedException, HttpFailureException;
}
