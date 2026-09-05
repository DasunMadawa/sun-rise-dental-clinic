package dao.custom.impl;

import model.AppointmentModel;
import model.DentistModel;
import model.PatientModel;
import model.TreatmentTypeModel;
import model.enums.AppointmentStatus;
import model.enums.Gender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.DBConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * <b>Integration test - needs a live MySQL instance matching util.DBConnection's
 * credentials. Named *IT so `mvn test` skips it by default (Surefire only picks up
 * *Test); run it deliberately with a disposable/dev database.</b>
 * <p>
 * This is the regression test for the FK violation:
 * "Cannot add or update a child row ... FOREIGN KEY (dentist_id) REFERENCES dentist
 * (dentist_id)". AppointmentDAOImpl.add()/update() used to bind
 * {@code appointment.getDentist().getUserID()} into the appointment.dentist_id
 * column, which only happened to work when a dentist's user_id and dentist_id were
 * equal. The fix binds {@code getDentistID()} instead. To make sure a future edit
 * can't silently reintroduce that mix-up, this test deliberately uses a dentist
 * whose user_id and dentist_id are different values, then asserts (a) the save
 * succeeds at all - it used to throw before the fix - and (b) the stored dentist_id
 * matches the dentist's own id, not their user id.
 * <p>
 * The test creates its own user/dentist/patient/appointment rows with a unique
 * "IT_" prefix and removes them in @AfterEach, so it does not depend on - or
 * leave behind anything in - the application's real seed/demo data.
 */
class AppointmentDentistIdIT {

    private static final String USER_ID = "IT_USR1";
    private static final String DENTIST_ID = "IT_DEN1"; // deliberately different from USER_ID
    private static final String PATIENT_ID = "IT_PT01";
    private static final String APPOINTMENT_NO = "IT_APT1";

    @Test
    @DisplayName("appointment.dentist_id is saved as the dentist's own id, not their user id")
    void savedAppointment_usesDentistIdNotUserId() throws Exception {
        seedUserAndDentist();
        seedPatient();

        DentistModel dentist = new DentistModel(
                USER_ID, "it.dentist", PasswordUtil.hash("Password123"), true, null, "it.dentist@example.com",
                DENTIST_ID, "IT Test Dentist", "General", "0771234567", 1000.0, java.util.Collections.emptyList()
        );
        PatientModel patient = new PatientModel(
                PATIENT_ID, "IT Test Patient", "Colombo", "0771112222", "991234567V",
                LocalDate.of(1990, 1, 1), Gender.MALE, LocalDate.now(), null
        );
        TreatmentTypeModel checking = TreatmentTypeModel.fromCodeOrDefault("CHECKING");

        AppointmentModel appointment = new AppointmentModel(
                APPOINTMENT_NO, patient, dentist, null, checking, 1,
                LocalDate.now().plusDays(30), LocalTime.of(9, 0), AppointmentStatus.SCHEDULED, "integration test"
        );

        // Before the fix, this threw SQLIntegrityConstraintViolationException.
        appointment.save();

        String storedDentistId = readStoredDentistId();
        assertEquals(DENTIST_ID, storedDentistId);
        assertNotEquals(USER_ID, storedDentistId);
    }

    private void seedUserAndDentist() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement userStmt = connection.prepareStatement(
                "INSERT INTO user (user_id, username, password_hash, role, is_active, email) VALUES (?, ?, ?, 'DENTIST', TRUE, ?)")) {
            userStmt.setString(1, USER_ID);
            userStmt.setString(2, "it.dentist");
            userStmt.setString(3, PasswordUtil.hash("Password123"));
            userStmt.setString(4, "it.dentist@example.com");
            userStmt.executeUpdate();
        }
        try (PreparedStatement dentistStmt = connection.prepareStatement(
                "INSERT INTO dentist (dentist_id, user_id, dentist_name, specialization, contact_no, consultation_fee, available_days) " +
                        "VALUES (?, ?, 'IT Test Dentist', 'General', '0771234567', 1000.00, '')")) {
            dentistStmt.setString(1, DENTIST_ID);
            dentistStmt.setString(2, USER_ID);
            dentistStmt.executeUpdate();
        }
    }

    private void seedPatient() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO patient (patient_id, patient_name, address, contact_no, nic, date_of_birth, gender, registered_date) " +
                        "VALUES (?, 'IT Test Patient', 'Colombo', '0771112222', '991234567V', '1990-01-01', 'MALE', CURDATE())")) {
            statement.setString(1, PATIENT_ID);
            statement.executeUpdate();
        }
    }

    private String readStoredDentistId() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT dentist_id FROM appointment WHERE appointment_no=?")) {
            statement.setString(1, APPOINTMENT_NO);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getString("dentist_id");
        }
    }

    @AfterEach
    void cleanUp() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM appointment WHERE appointment_no='" + APPOINTMENT_NO + "'");
            statement.executeUpdate("DELETE FROM patient WHERE patient_id='" + PATIENT_ID + "'");
            statement.executeUpdate("DELETE FROM dentist WHERE dentist_id='" + DENTIST_ID + "'");
            statement.executeUpdate("DELETE FROM user WHERE user_id='" + USER_ID + "'");
        }
    }
}
