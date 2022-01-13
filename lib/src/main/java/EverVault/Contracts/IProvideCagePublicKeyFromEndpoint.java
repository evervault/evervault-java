package EverVault.Contracts;

import EverVault.Exceptions.HttpFailureException;
import EverVault.ReadModels.CagePublicKey;

import java.io.IOException;
import java.util.HashMap;

public interface IProvideCagePublicKeyFromEndpoint {
    CagePublicKey getCagePublicKeyFromEndpoint(String url) throws IOException, InterruptedException, HttpFailureException;
    CagePublicKey getCagePublicKeyFromEndpoint(String url, HashMap<String, String> headerMap) throws IOException, InterruptedException, HttpFailureException;
}
