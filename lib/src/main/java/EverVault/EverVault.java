/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package EverVault;

import EverVault.Contracts.IProvideCagePublicKey;
import EverVault.Contracts.IProvideEncryptionForObject;
import EverVault.Exceptions.UndefinedDataException;

import java.util.Dictionary;

/// TODO
public class EverVault {
    private final String everVaultApi;
    private final String apiKey;
    private final IProvideCagePublicKey cagePublicKeyProvider;
    private final IProvideEncryptionForObject encryptionProvider;

    public EverVault(String everVaultApi,
                     String apiKey,
                     IProvideCagePublicKey providesCagePublicKey,
                     IProvideEncryptionForObject provideEncryption) {
        this.everVaultApi = everVaultApi;
        this.apiKey = apiKey;
        this.cagePublicKeyProvider = providesCagePublicKey;
        this.encryptionProvider = provideEncryption;
    }

    public Object encrypt(Object data) throws Exception {
        if (data == null) {
            throw new UndefinedDataException();
        }

        var cagePublicKey = cagePublicKeyProvider.getCagePublicKey(everVaultApi);

        encryptionProvider.encrypt(data);

        return new String("Test");
    }

    public void Run(String cageName, Dictionary<String, String> data, Dictionary<String, String> options) {
        /// TODO
    }
}
