/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package EverVault;

import EverVault.Exceptions.UndefinedDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WhenEncryptingDataTests {
    @Test
    void encryptingNumberGeneratesEvNumberType() throws Exception {
        final int data = 1;

        EverVault everVault = new EverVault("test");
        var encryptedData = everVault.Encrypt(data);
        assertEverVaultNumber(encryptedData);
    }

    @Test
    void tryingToEncryptNullThrows() {
        EverVault everVault = new EverVault("test");
        assertThrows(UndefinedDataException.class, () -> everVault.Encrypt(null));
    }

    void assertEverVaultString(String data) {
        var splitted = data.split("\\.");
        assertEquals(6, splitted.length);
    }

    void assertEverVaultNumber(String data) {
        var splitted = data.split("\\.");
        assertEquals(7, splitted.length);
    }
}