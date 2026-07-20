package gov.lawenforcement.common.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Pseudonymizer {

    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

    public static String pseudonymize(String realValue, String salt) {
        String cacheKey = realValue + salt;
        return CACHE.computeIfAbsent(cacheKey, key -> {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
                StringBuilder hex = new StringBuilder();
                for (int i = 0; i < 8; i++) {
                    hex.append(String.format("%02x", hash[i]));
                }
                return "PSEUDO_" + hex;
            } catch (Exception e) {
                return "PSEUDO_ERROR";
            }
        });
    }
}
