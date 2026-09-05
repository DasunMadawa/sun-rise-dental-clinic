package model;

import model.enums.AppointmentStatus;
import model.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure business-logic tests for {@link AppointmentModel}.
 * <p>
 * None of these touch the database - every {@link PatientModel}, {@link DentistModel}
 * and {@link TreatmentTypeModel} here is a plain object built with {@code new}, which is
 * exactly why AppointmentModel keeps its pricing/duration/overlap math as instance
 * methods instead of burying it in a DAO: the rule "no manual price typing at billing
 * time" from docs/SequenceDiagrams.md only holds if that math is trustworthy on its own,
 * independent of whatever is or isn't in MySQL that day.
 */
class AppointmentBusinessLogicTest {

    private DentistModel dentist;
    private PatientModel patient;
    private TreatmentTypeModel filling;   // per-tooth: unitCost 6000, 45 min
    private TreatmentTypeModel checking;  // flat fee: unitCost 0, 15 min

    @BeforeEach
    void setUp() {
        dentist = new DentistModel(
                "USR0002", "dr.silva", "hash", true, null, "dr.silva@example.com",
                "DEN001", "Dr. Silva", "General", "0771234567", 1500.0,
                Collections.singletonList("MONDAY")
        );
        patient = new PatientModel(
                "PT001", "Kasun Perera", "Colombo", "0771112222", "991234567V",
                LocalDate.of(1995, 1, 1), Gender.MALE, LocalDate.now(), "kasun@example.com"
        );
        filling = new TreatmentTypeModel("FILLING", "Filling", 6000.0, true, 45);
        checking = new TreatmentTypeModel("CHECKING", "Checking", 0.0, false, 15);
    }

    private AppointmentModel appointment(String no, TreatmentTypeModel treatment, int noTooth,
                                          LocalDate date, LocalTime time, AppointmentStatus status) {
        return new AppointmentModel(no, patient, dentist, "STF001", treatment, noTooth, date, time, status, "");
    }

    @Nested
    @DisplayName("Cost and duration math")
    class CostAndDuration {

        @Test
        @DisplayName("per-tooth treatment cost scales with tooth count (this is the number billing prints - no manual entry)")
        void treatmentCost_scalesPerTooth() {
            AppointmentModel apt = appointment("APT0001", filling, 3, LocalDate.now(), LocalTime.of(9, 0), AppointmentStatus.SCHEDULED);
            assertEquals(18000.0, apt.getTreatmentCost());
        }

        @Test
        @DisplayName("flat-fee treatment cost ignores tooth count")
        void treatmentCost_flatFeeIgnoresToothCount() {
            AppointmentModel apt = appointment("APT0002", checking, 5, LocalDate.now(), LocalTime.of(9, 0), AppointmentStatus.SCHEDULED);
            assertEquals(0.0, apt.getTreatmentCost());
        }

        @Test
        @DisplayName("duration scales with tooth count for per-tooth treatments")
        void duration_scalesPerTooth() {
            AppointmentModel apt = appointment("APT0003", filling, 3, LocalDate.now(), LocalTime.of(9, 0), AppointmentStatus.SCHEDULED);
            assertEquals(135, apt.getDurationMinutes()); // 45 min * 3 teeth
        }

        @Test
        @DisplayName("end time is start time plus the computed duration")
        void endTime_isStartPlusDuration() {
            AppointmentModel apt = appointment("APT0004", filling, 3, LocalDate.now(), LocalTime.of(9, 0), AppointmentStatus.SCHEDULED);
            assertEquals(LocalTime.of(11, 15), apt.getEndTime()); // 09:00 + 135 min
        }

        @Test
        @DisplayName("consultation fee is read straight from the dentist, not typed in")
        void consultationFee_comesFromDentist() {
            AppointmentModel apt = appointment("APT0005", checking, 1, LocalDate.now(), LocalTime.of(9, 0), AppointmentStatus.SCHEDULED);
            assertEquals(1500.0, apt.getConsultationFee());
        }
    }

    @Nested
    @DisplayName("Double-booking clash detection (docs/SequenceDiagrams.md, Figure 3b)")
    class ClashDetection {

        @Test
        @DisplayName("two SCHEDULED appointments with overlapping time ranges on the same day clash")
        void overlaps_trueForOverlappingScheduledSlots() {
            // 09:00-11:15 (filling x3, 135 min) vs 10:00-10:15 (checking, 15 min) - overlaps
            AppointmentModel first = appointment("APT0001", filling, 3, LocalDate.of(2026, 9, 10), LocalTime.of(9, 0), AppointmentStatus.SCHEDULED);
            AppointmentModel second = appointment("APT0002", checking, 1, LocalDate.of(2026, 9, 10), LocalTime.of(10, 0), AppointmentStatus.SCHEDULED);

            assertTrue(first.overlaps(second));
            assertTrue(second.overlaps(first)); // symmetric
        }

        @Test
        @DisplayName("back-to-back appointments (end == next start) do not clash")
        void overlaps_falseForAdjacentSlots() {
            // 09:00-09:15 (checking) then 09:15-09:30 (checking) - touching, not overlapping
            AppointmentModel first = appointment("APT0001", checking, 1, LocalDate.of(2026, 9, 10), LocalTime.of(9, 0), AppointmentStatus.SCHEDULED);
            AppointmentModel second = appointment("APT0002", checking, 1, LocalDate.of(2026, 9, 10), LocalTime.of(9, 15), AppointmentStatus.SCHEDULED);

            assertFalse(first.overlaps(second));
        }

        @Test
        @DisplayName("same time, different day never clashes")
        void overlaps_falseForDifferentDates() {
            AppointmentModel first = appointment("APT0001", filling, 1, LocalDate.of(2026, 9, 10), LocalTime.of(9, 0), AppointmentStatus.SCHEDULED);
            AppointmentModel second = appointment("APT0002", filling, 1, LocalDate.of(2026, 9, 11), LocalTime.of(9, 0), AppointmentStatus.SCHEDULED);

            assertFalse(first.overlaps(second));
        }

        @Test
        @DisplayName("a CANCELLED appointment no longer blocks its old slot")
        void overlaps_falseWhenOneSideIsCancelled() {
            AppointmentModel cancelled = appointment("APT0001", filling, 3, LocalDate.of(2026, 9, 10), LocalTime.of(9, 0), AppointmentStatus.CANCELLED);
            AppointmentModel newBooking = appointment("APT0002", checking, 1, LocalDate.of(2026, 9, 10), LocalTime.of(9, 30), AppointmentStatus.SCHEDULED);

            assertFalse(cancelled.overlaps(newBooking));
            assertFalse(newBooking.overlaps(cancelled));
        }

        @Test
        @DisplayName("a NO_SHOW appointment no longer blocks its old slot either")
        void overlaps_falseWhenOneSideIsNoShow() {
            AppointmentModel noShow = appointment("APT0001", filling, 3, LocalDate.of(2026, 9, 10), LocalTime.of(9, 0), AppointmentStatus.NO_SHOW);
            AppointmentModel newBooking = appointment("APT0002", checking, 1, LocalDate.of(2026, 9, 10), LocalTime.of(9, 30), AppointmentStatus.SCHEDULED);

            assertFalse(noShow.overlaps(newBooking));
        }
    }
}
