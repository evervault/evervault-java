package EverVault;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class EncryptSetup {
    private final String ALGORITHM = "EC";
    private final String STDNAME = "secp256k1";
    private final String KEYAGREEMENT_ALGORITHM = "ECDH";

    public KeyPair keyPair;
    public byte[] sharedKey;

    public EncryptSetup() throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException {
        var provider = new BouncyCastleProvider();
        var keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, provider);
        var genParameter = new ECGenParameterSpec(STDNAME);
        keyPairGenerator.initialize(genParameter, new SecureRandom());
        keyPair = keyPairGenerator.generateKeyPair();

        var agreement = KeyAgreement.getInstance(KEYAGREEMENT_ALGORITHM, provider);
        agreement.init(keyPair.getPrivate());
        agreement.doPhase(keyPair.getPublic(), true);

        sharedKey = agreement.generateSecret();
    }
}
