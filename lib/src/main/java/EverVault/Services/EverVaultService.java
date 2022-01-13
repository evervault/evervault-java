/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package EverVault.Services;

import EverVault.Contracts.*;
import EverVault.Exceptions.HttpFailureException;
import EverVault.Exceptions.NotPossibleToHandleDataTypeException;
import EverVault.Exceptions.UndefinedDataException;
import EverVault.ReadModels.GeneratedSharedKey;
import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

/// TODO
public class EverVaultService {
    private IProvideCagePublicKeyFromEndpoint cagePublicKeyFromEndpointProvider;
    private IProvideECPublicKey ecPublicKeyProvider;
    private IProvideSharedKey sharedKeyProvider;
    private IProvideEncryptionForObject encryptionProvider;

    protected PublicKey ecdhKey;
    protected GeneratedSharedKey sharedKey;

    protected static final String EVERVAULT_BASE_URL = "https://api.evervault.com/";

    protected void setupKeyProviders(IProvideCagePublicKeyFromEndpoint cagePublicKeyFromEndpointProvider,
                            IProvideECPublicKey ecPublicKeyProvider,
                            IProvideSharedKey sharedKeyProvider) throws HttpFailureException, InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InterruptedException {
        this.cagePublicKeyFromEndpointProvider = cagePublicKeyFromEndpointProvider;
        this.ecPublicKeyProvider = ecPublicKeyProvider;
        this.sharedKeyProvider = sharedKeyProvider;

        setupKeys();
    }

    protected void setupEncryption(IProvideEncryptionForObject encryptionProvider) {
        if (encryptionProvider == null) {
            throw new NullPointerException(IProvideSharedKey.class.getName());
        }

        this.encryptionProvider = encryptionProvider;
    }

    private void setupKeys() throws HttpFailureException, IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException {
        if (cagePublicKeyFromEndpointProvider == null) {
            throw new NullPointerException(IProvideCagePublicKeyFromEndpoint.class.getName());
        }

        if (ecPublicKeyProvider == null) {
            throw new NullPointerException(IProvideECPublicKey.class.getName());
        }

        if (sharedKeyProvider == null) {
            throw new NullPointerException(IProvideSharedKey.class.getName());
        }

        var cageKey = cagePublicKeyFromEndpointProvider.getCagePublicKeyFromEndpoint(EVERVAULT_BASE_URL);

        this.ecdhKey = ecPublicKeyProvider.getEllipticCurvePublicKeyFrom(cageKey.ecdhKey);

        this.sharedKey = sharedKeyProvider.generateSharedKeyBasedOn(this.ecdhKey);
    }

    public Object encrypt(Object data) throws NotPossibleToHandleDataTypeException, InvalidCipherTextException, IOException, UndefinedDataException {
        if (data == null) {
            throw new UndefinedDataException();
        }

        return this.encryptionProvider.encrypt(data);
    }
}
