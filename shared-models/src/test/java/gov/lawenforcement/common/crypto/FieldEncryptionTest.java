package gov.lawenforcement.common.crypto;

import org.junit.jupiter.api.Test;
import java.security.SecureRandom;
import static org.junit.jupiter.api.Assertions.*;

class FieldEncryptionTest {

    private static final byte[] KEY = new byte[32];

    static {
        new SecureRandom().nextBytes(KEY);
    }

    @Test
    void encryptDecrypt_roundTrip_success() {
        String original = "Sensitive Crime Data - Confidential";
        String encrypted = FieldEncryption.encrypt(original, KEY);
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
        assertNotEquals(original, encrypted);

        String decrypted = FieldEncryption.decrypt(encrypted, KEY);
        assertEquals(original, decrypted);
    }

    @Test
    void encryptDecrypt_emptyString() {
        String original = "";
        String encrypted = FieldEncryption.encrypt(original, KEY);
        String decrypted = FieldEncryption.decrypt(encrypted, KEY);
        assertEquals(original, decrypted);
    }

    @Test
    void encryptDecrypt_specialCharacters() {
        String original = "Crime@#$%^&*()_+:\"<>?,./;'[]-=";
        String encrypted = FieldEncryption.encrypt(original, KEY);
        String decrypted = FieldEncryption.decrypt(encrypted, KEY);
        assertEquals(original, decrypted);
    }

    @Test
    void encryptDecrypt_unicode() {
        String original = "ಕನ್ನಡ ಟೆಸ್ಟ್ crime data ñoño";
        String encrypted = FieldEncryption.encrypt(original, KEY);
        String decrypted = FieldEncryption.decrypt(encrypted, KEY);
        assertEquals(original, decrypted);
    }

    @Test
    void encryptDecrypt_longText() {
        String original = "A".repeat(10000);
        String encrypted = FieldEncryption.encrypt(original, KEY);
        String decrypted = FieldEncryption.decrypt(encrypted, KEY);
        assertEquals(original, decrypted);
    }

    @Test
    void encrypt_producesDifferentCiphertexts() {
        String original = "Same data each time";
        String encrypted1 = FieldEncryption.encrypt(original, KEY);
        String encrypted2 = FieldEncryption.encrypt(original, KEY);
        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    void decrypt_withWrongKey_fails() {
        String original = "Sensitive data";
        String encrypted = FieldEncryption.encrypt(original, KEY);
        byte[] wrongKey = new byte[32];
        assertThrows(RuntimeException.class, () -> FieldEncryption.decrypt(encrypted, wrongKey));
    }

    @Test
    void decrypt_withInvalidData_fails() {
        assertThrows(RuntimeException.class, () -> FieldEncryption.decrypt("invalid-base64!!", KEY));
    }

    @Test
    void encrypt_withNull_throwsException() {
        assertThrows(RuntimeException.class, () -> FieldEncryption.encrypt(null, KEY));
    }
}
