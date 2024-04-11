package com.evervault;

import com.evervault.exceptions.NotImplementedException;
import com.evervault.services.EncryptionServiceCommon;
import org.junit.jupiter.api.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class WhenGeneratingAKeyPairTests {
    private static class SomeNewService extends EncryptionServiceCommon {
        public KeyPair newKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NotImplementedException {
            return this.generateNewKeyPair();
        }

        protected static final String CURVE_NAME_256K1 = "secp256k1";
        protected static final String KEY_GENERATOR_ALGORITHM = "EC";

        @Override
        protected String getCurveName() {
            return CURVE_NAME_256K1;
        }

        @Override
        protected String getKeyGeneratorAlgorithm() throws NotImplementedException {
            return KEY_GENERATOR_ALGORITHM;
        }
    }

    private static final String ELLIPTIC_CURVE_ALGORITHM = "EC";

    @Test
    void keyPairAlgorithmMustMatch() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NotImplementedException {
        SomeNewService service = new SomeNewService();

        KeyPair keypair = service.newKeyPair();

        assert ELLIPTIC_CURVE_ALGORITHM.equals(keypair.getPublic().getAlgorithm());
    }
}
