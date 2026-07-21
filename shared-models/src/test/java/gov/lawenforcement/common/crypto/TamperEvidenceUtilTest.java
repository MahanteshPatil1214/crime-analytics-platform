package gov.lawenforcement.common.crypto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TamperEvidenceUtilTest {

    @Test
    void computeHmac_returnsConsistentHash() {
        String data = "test-data-for-hmac";
        String hash1 = TamperEvidenceUtil.computeHmac(data);
        String hash2 = TamperEvidenceUtil.computeHmac(data);
        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length());
    }

    @Test
    void computeHmac_differentInputs_differentHashes() {
        String hash1 = TamperEvidenceUtil.computeHmac("input-A");
        String hash2 = TamperEvidenceUtil.computeHmac("input-B");
        assertNotEquals(hash1, hash2);
    }

    @Test
    void computeHmac_emptyString() {
        String hash = TamperEvidenceUtil.computeHmac("");
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    void computeHmac_unicodeInput() {
        String hash = TamperEvidenceUtil.computeHmac("crime डेटा ಕನ್ನಡ");
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    void computeChainedSeal_producesValidSeal() {
        Instant now = Instant.now();
        String seal = TamperEvidenceUtil.computeChainedSeal(
                "prev-seal-123", "user-456", "UPDATE",
                "CaseMaster", "789", now);
        assertNotNull(seal);
        assertEquals(64, seal.length());
    }

    @Test
    void computeChainedSeal_differentPreviousSeal_differentResult() {
        Instant now = Instant.now();
        String seal1 = TamperEvidenceUtil.computeChainedSeal("seal-A", "user-1", "CREATE", "Case", "1", now);
        String seal2 = TamperEvidenceUtil.computeChainedSeal("seal-B", "user-1", "CREATE", "Case", "1", now);
        assertNotEquals(seal1, seal2);
    }

    @Test
    void computeChainedSeal_differentTimestamps_differentResult() {
        String seal1 = TamperEvidenceUtil.computeChainedSeal("seal", "user", "CREATE", "Case", "1", Instant.ofEpochSecond(1000));
        String seal2 = TamperEvidenceUtil.computeChainedSeal("seal", "user", "CREATE", "Case", "1", Instant.ofEpochSecond(2000));
        assertNotEquals(seal1, seal2);
    }
}
