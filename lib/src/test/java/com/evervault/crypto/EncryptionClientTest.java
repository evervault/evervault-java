package com.evervault.crypto;

import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class EncryptionClientTest {

    @Test
    public void test() throws InvalidAlgorithmParameterException, UnsupportedEncodingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        EncryptionClient client = new EncryptionClient(new AppPublicKey("BGxhTCH37Idfvpoun+7fVEQOjb/Q8tXaOlso7eMwdB2RJuDn+VK9ubmf+nb6ezrXNXWbCZubCZPwWsLz+Zj+8TM="));
    }

}
