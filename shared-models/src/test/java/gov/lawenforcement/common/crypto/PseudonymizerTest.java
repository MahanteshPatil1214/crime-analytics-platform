package gov.lawenforcement.common.crypto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PseudonymizerTest {

    @Test
    void pseudonymize_returnsPseudoPrefixed() {
        String result = Pseudonymizer.pseudonymize("Rajesh Kumar", "salt-123");
        assertTrue(result.startsWith("PSEUDO_"));
    }

    @Test
    void pseudonymize_differentSalt_differentResult() {
        String result1 = Pseudonymizer.pseudonymize("Rajesh Kumar", "salt-A");
        String result2 = Pseudonymizer.pseudonymize("Rajesh Kumar", "salt-B");
        assertNotEquals(result1, result2);
    }

    @Test
    void pseudonymize_differentNames_differentResult() {
        String result1 = Pseudonymizer.pseudonymize("Rajesh Kumar", "salt");
        String result2 = Pseudonymizer.pseudonymize("Suresh Kumar", "salt");
        assertNotEquals(result1, result2);
    }

    @Test
    void pseudonymize_sameInput_sameOutput() {
        String result1 = Pseudonymizer.pseudonymize("Priya Sharma", "fixed-salt");
        String result2 = Pseudonymizer.pseudonymize("Priya Sharma", "fixed-salt");
        assertEquals(result1, result2);
    }

    @Test
    void pseudonymize_emptyString() {
        String result = Pseudonymizer.pseudonymize("", "salt");
        assertTrue(result.startsWith("PSEUDO_"));
    }

    @Test
    void pseudonymize_nullSalt() {
        String result1 = Pseudonymizer.pseudonymize("name", null);
        String result2 = Pseudonymizer.pseudonymize("name", null);
        assertEquals(result1, result2);
    }
}
