package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for the id-format regexes in {@link Validations}.
 * <p>
 * The patient-id half of this class exists specifically because of the
 * "Duplicate entry '' for key 'patient.PRIMARY'" bug worked in this session:
 * the registration screen never checked that a Patient Id had actually been
 * generated before saving. {@link Validations#patientPattern} is the rule
 * that says what a *valid* id looks like, so it is the natural place to pin
 * down, in code, that an empty string must never pass and that the id format
 * changed from "PAT0001" to "PT001".
 */
class ValidationsTest {

    @ParameterizedTest(name = "\"{0}\" is a valid Patient Id")
    @ValueSource(strings = {"PT001", "PT002", "PT999"})
    @DisplayName("Patient Id accepts the current PT### format")
    void patientPattern_acceptsCurrentFormat(String candidate) {
        assertTrue(Validations.patientPattern.matcher(candidate).matches());
    }

    @ParameterizedTest(name = "\"{0}\" is rejected")
    @ValueSource(strings = {
            "",             // the exact value that caused the duplicate-key crash
            "PAT0001",      // the old, retired format
            "PT01",         // too few digits
            "PT0001",       // too many digits
            "pt001",        // wrong case
            "PT-001"        // hyphenated form the user asked about and we rejected
    })
    @DisplayName("Patient Id rejects empty values, the old format, and near-misses")
    void patientPattern_rejectsInvalidValues(String candidate) {
        assertFalse(Validations.patientPattern.matcher(candidate).matches());
    }

    @ParameterizedTest
    @CsvSource({
            "DEN001, true",
            "DEN12,  false",
            "DEN0012,false"
    })
    @DisplayName("Dentist Id follows DEN### ")
    void dentistPattern(String candidate, boolean expected) {
        assertEquals(expected, Validations.dentistPattern.matcher(candidate).matches());
    }

    @Test
    @DisplayName("Appointment No follows APT#### (matches AppointmentDAOImpl.generateNextId)")
    void appointmentPattern_acceptsGeneratedShape() {
        assertTrue(Validations.appointmentPattern.matcher("APT0001").matches());
        assertFalse(Validations.appointmentPattern.matcher("APT1").matches());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0771234567", "+94771234567", "94771234567", "0094771234567", "0112345678"})
    @DisplayName("Mobile pattern accepts real Sri Lankan mobile/land numbers used as sample data")
    void mobilePattern_acceptsRealNumbers(String candidate) {
        assertTrue(Validations.mobilePattern.matcher(candidate).matches());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "077123456", "abcdefghij"})
    @DisplayName("Mobile pattern rejects obviously malformed numbers")
    void mobilePattern_rejectsGarbage(String candidate) {
        assertFalse(Validations.mobilePattern.matcher(candidate).matches());
    }

    @ParameterizedTest
    @ValueSource(strings = {"991234567V", "199912345678"})
    @DisplayName("NIC pattern accepts old (9+V/X) and new (12-digit) Sri Lankan NIC formats")
    void nicPattern_acceptsBothEras(String candidate) {
        assertTrue(Validations.nicPattern.matcher(candidate).matches());
    }
}
