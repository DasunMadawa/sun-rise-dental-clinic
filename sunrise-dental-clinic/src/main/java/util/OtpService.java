package util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OtpService {
    private static final int EXPIRY_MINUTES = 5;
    private static final Map<String, OtpEntry> otps = new ConcurrentHashMap<>();
    private static final SecureRandom random = new SecureRandom();

    public static String generate(String userId) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        otps.put(userId, new OtpEntry(code, LocalDateTime.now().plusMinutes(EXPIRY_MINUTES)));
        return code;
    }

    public static boolean verify(String userId, String code) {
        OtpEntry entry = otps.get(userId);
        if (entry == null || entry.expiresAt.isBefore(LocalDateTime.now())) {
            return false;
        }

        boolean matches = entry.code.equals(code);
        if (matches) {
            otps.remove(userId);
        }
        return matches;
    }

    private static class OtpEntry {
        private final String code;
        private final LocalDateTime expiresAt;

        private OtpEntry(String code, LocalDateTime expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }

    }

}
