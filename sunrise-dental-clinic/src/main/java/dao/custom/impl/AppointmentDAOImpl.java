package dao.custom.impl;

import dao.custom.AppointmentDAO;
import dao.custom.PatientDAO;
import dao.custom.UserDAO;
import model.AppointmentModel;
import model.DentistModel;
import model.PatientModel;
import model.enums.AppointmentStatus;
import model.enums.TreatmentType;
import model.UserModel;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAOImpl implements AppointmentDAO {
    private static final String SELECT_ALL = "SELECT * FROM appointment";

    PatientDAO patientDAO = new PatientDAOImpl();
    UserDAO userDAO = new UserDAOImpl();

    @Override
    public boolean add(AppointmentModel appointment) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO appointment (appointment_no, patient_id, dentist_id, booked_by_staff_id, treatment_code, no_tooth, appointment_date, appointment_time, status, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            bindAppointment(statement, appointment);

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public AppointmentModel search(String id) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL + " WHERE appointment_no=?");
            statement.setString(1, id);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? map(resultSet) : null;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public boolean update(AppointmentModel appointment) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE appointment SET patient_id=?, dentist_id=?, booked_by_staff_id=?, treatment_code=?, no_tooth=?, appointment_date=?, appointment_time=?, status=?, remarks=? WHERE appointment_no=?"
            );
            statement.setString(1, appointment.getPatient().getPatientID());
            statement.setString(2, appointment.getDentist().getUserID());
            statement.setString(3, appointment.getBookedByStaffID());
            statement.setString(4, appointment.getTreatment().getCode());
            statement.setInt(5, appointment.getNoTooth());
            statement.setDate(6, java.sql.Date.valueOf(appointment.getAppointmentDate()));
            statement.setTime(7, java.sql.Time.valueOf(appointment.getAppointmentTime()));
            statement.setString(8, appointment.getStatus().name());
            statement.setString(9, appointment.getRemarks());
            statement.setString(10, appointment.getAppointmentNo());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public boolean delete(String id) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM appointment WHERE appointment_no=?");
            statement.setString(1, id);

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public List<AppointmentModel> getAll() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL + " ORDER BY appointment_no");

            List<AppointmentModel> appointments = new ArrayList<>();
            while (resultSet.next()) {
                appointments.add(map(resultSet));
            }
            return appointments;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public List<AppointmentModel> getByDentistAndDate(String dentistId, LocalDate date) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL + " WHERE dentist_id=? AND appointment_date=?");
            statement.setString(1, dentistId);
            statement.setDate(2, java.sql.Date.valueOf(date));

            ResultSet resultSet = statement.executeQuery();
            List<AppointmentModel> appointments = new ArrayList<>();
            while (resultSet.next()) {
                appointments.add(map(resultSet));
            }
            return appointments;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public AppointmentModel getLatestForPatient(String patientId) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    SELECT_ALL + " WHERE patient_id=? ORDER BY appointment_date DESC, appointment_time DESC LIMIT 1"
            );
            statement.setString(1, patientId);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? map(resultSet) : null;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public String generateNextId() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT appointment_no FROM appointment ORDER BY appointment_no DESC LIMIT 1");

        if (!resultSet.next()) {
            return "APT0001";
        }
        String lastId = resultSet.getString("appointment_no");
        return String.format("APT%04d", Integer.parseInt(lastId.substring(3)) + 1);
    }

    private void bindAppointment(PreparedStatement statement, AppointmentModel appointment) throws Exception {
        statement.setString(1, appointment.getAppointmentNo());
        statement.setString(2, appointment.getPatient().getPatientID());
        statement.setString(3, appointment.getDentist().getUserID());
        statement.setString(4, appointment.getBookedByStaffID());
        statement.setString(5, appointment.getTreatment().getCode());
        statement.setInt(6, appointment.getNoTooth());
        statement.setDate(7, java.sql.Date.valueOf(appointment.getAppointmentDate()));
        statement.setTime(8, java.sql.Time.valueOf(appointment.getAppointmentTime()));
        statement.setString(9, appointment.getStatus().name());
        statement.setString(10, appointment.getRemarks());
    }

    private AppointmentModel map(ResultSet resultSet) throws Exception {
        PatientModel patient = patientDAO.search(resultSet.getString("patient_id"));
        UserModel dentistUser = userDAO.search(resultSet.getString("dentist_id"));

        return new AppointmentModel(
                resultSet.getString("appointment_no"),
                patient,
                (DentistModel) dentistUser,
                resultSet.getString("booked_by_staff_id"),
                TreatmentType.fromCode(resultSet.getString("treatment_code")),
                resultSet.getInt("no_tooth"),
                resultSet.getDate("appointment_date").toLocalDate(),
                resultSet.getTime("appointment_time").toLocalTime(),
                AppointmentStatus.valueOf(resultSet.getString("status")),
                resultSet.getString("remarks")
        );
    }

}
