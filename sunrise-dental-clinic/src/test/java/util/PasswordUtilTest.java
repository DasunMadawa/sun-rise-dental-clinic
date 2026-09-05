package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link PasswordUtil#hash} is what turns a typed-in password into what
 * actually sits in the {@code user.password_hash} column, and it is what the
 * OTP/forgot-password flow and every login check depend on. The most useful
 * thing a test can do here is not just "does it hash" but "does it hash to
 * the exact value schema.sql seeds for the admin account" — if this test
 * ever goes red, the seeded admin login (admin / admin123) breaks too.
 */
class PasswordUtilTest {

    private static final String SEEDED_ADMIN_HASH =
            "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";

    @Test
    @DisplayName("hash(\"admin123\") matches the value seeded for the admin account in schema.sql")
    void hash_matchesSeededAdminAccount() {
        assertEquals(SEEDED_ADMIN_HASH, PasswordUtil.hash("admin123"));
    }

    @Test
    @DisplayName("hashing is deterministic - same input always gives the same hash")
    void hash_isDeterministic() {
        assertEquals(PasswordUtil.hash("SamePassword1"), PasswordUtil.hash("SamePassword1"));
    }

    @Test
    @DisplayName("different passwords never collide for our sample inputs")
    void hash_differentInputsGiveDifferentHashes() {
        assertNotEquals(PasswordUtil.hash("SamePassword1"), PasswordUtil.hash("SamePassword2"));
    }

    @Test
    @DisplayName("output is always 64 lowercase hex characters (fits the password_hash VARCHAR(64) column)")
    void hash_isSixtyFourLowercaseHexChars() {
        String hash = PasswordUtil.hash("whatever the user types");
        assertEquals(64, hash.length());
        assertTrue(hash.matches("^[0-9a-f]{64}$"), "Hash is not 64 lowercase hex chars: " + hash);
    }
}
