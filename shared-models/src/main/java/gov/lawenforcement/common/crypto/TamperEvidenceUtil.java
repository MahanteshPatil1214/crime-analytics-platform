package gov.lawenforcement.common.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class TamperEvidenceUtil {

    private static final String HMAC_KEY = System.getenv("AUDIT_HMAC_SECRET");

    public static String computeChainedSeal(String previousSeal, String userId, String action,
                                             String entityType, String entityId, Instant timestamp) {
        String data = String.join("|", previousSeal, userId, action, entityType, entityId, timestamp.toString());
        return computeHmac(data);
    }

    public static String computeHmac(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(HMAC_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC computation failed", e);
        }
    }
}
