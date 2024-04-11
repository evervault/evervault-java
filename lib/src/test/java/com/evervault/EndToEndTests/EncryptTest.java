package com.evervault.EndToEndTests;

import com.evervault.exceptions.EvervaultException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncryptTest extends EndToEndTest {

    @Test
    void it_encrypt_and_decrypt_a_string_successfully() throws EvervaultException {
        String str = "Hello World!";
        String encrypted = (String) evervault.encrypt(str);
        String decrypted = evervault.decrypt(encrypted, String.class);
        assertEquals(str, decrypted);
    }

    @Test
    void it_encrypt_and_decrypt_a_char_successfully() throws EvervaultException {
        char character = 'j';
        String encrypted = (String) evervault.encrypt(character);
        Character decrypted = evervault.decrypt(encrypted, Character.class);
        assertEquals(character, decrypted);
    }

    @Test
    void it_encrypt_and_decrypt_a_short_successfully() throws EvervaultException {
        short number = 2;
        String encrypted = (String) evervault.encrypt(number);
        Short decrypted = evervault.decrypt(encrypted, Short.class);
        assertEquals(number, decrypted);
    }

    @Test
    void it_encrypt_and_decrypt_an_integer_successfully() throws EvervaultException {
        int number = 2;
        String encrypted = (String) evervault.encrypt(number);
        Integer decrypted = evervault.decrypt(encrypted, Integer.class);
        assertEquals(number, decrypted);
    }

    @Test
    void it_encrypt_and_decrypt_a_float_successfully() throws EvervaultException {
        float number = 15496552236523322f;
        String encrypted = (String) evervault.encrypt(number);
        Float decrypted = evervault.decrypt(encrypted, Float.class);
        assertEquals(number, decrypted);
    }

    @Test
    void it_encrypt_and_decrypt_a_double_successfully() throws EvervaultException {
        double number = 3.145698;
        String encrypted = (String) evervault.encrypt(number);
        Double decrypted = evervault.decrypt(encrypted, Double.class);
        assertEquals(number, decrypted);
    }

    @Test
    void it_encrypt_and_decrypt_true_successfully() throws EvervaultException {
        Boolean bool = true;
        String encrypted = (String) evervault.encrypt(bool);
        Boolean decrypted = evervault.decrypt(encrypted, Boolean.class);
        assertEquals(bool, decrypted);
    }

    @Test
    void it_encrypt_and_decrypt_false_successfully() throws EvervaultException {
        Boolean bool = false;
        String encrypted = (String) evervault.encrypt(bool);
        Boolean decrypted = evervault.decrypt(encrypted, Boolean.class);
        assertEquals(bool, decrypted);
    }
}
