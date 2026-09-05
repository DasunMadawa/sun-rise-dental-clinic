package dao.custom.impl;

import dao.custom.PatientDAO;
import model.enums.Gender;
import model.PatientModel;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PatientDAOImpl implements PatientDAO {

    @Override
    public boolean add(PatientModel patient) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO patient (patient_id, patient_name, address, contact_no, nic, date_of_birth, gender, registered_date, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            statement.setString(1, patient.getPatientID());
            statement.setString(2, patient.getPatientName());
            statement.setString(3, patient.getAddress());
            statement.setString(4, patient.getContactNo());
            statement.setString(5, patient.getNic());
            statement.setDate(6, java.sql.Date.valueOf(patient.getDateOfBirth()));
            statement.setString(7, patient.getGender().name());
            statement.setDate(8, java.sql.Date.valueOf(patient.getRegisteredDate()));
            statement.setString(9, patient.getEmail());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public PatientModel search(String id) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM patient WHERE patient_id=?");
            statement.setString(1, id);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? map(resultSet) : null;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public PatientModel searchByNic(String nic) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM patient WHERE nic=?");
            statement.setString(1, nic);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? map(resultSet) : null;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public boolean update(PatientModel patient) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE patient SET patient_name=?, address=?, contact_no=?, nic=?, date_of_birth=?, gender=?, email=? WHERE patient_id=?"
            );
            statement.setString(1, patient.getPatientName());
            statement.setString(2, patient.getAddress());
            statement.setString(3, patient.getContactNo());
            statement.setString(4, patient.getNic());
            statement.setDate(5, java.sql.Date.valueOf(patient.getDateOfBirth()));
            statement.setString(6, patient.getGender().name());
            statement.setString(7, patient.getEmail());
            statement.setString(8, patient.getPatientID());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public boolean delete(String id) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM patient WHERE patient_id=?");
            statement.setString(1, id);

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public List<PatientModel> getAll() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM patient ORDER BY patient_id");
            ResultSet resultSet = statement.executeQuery();

            List<PatientModel> patients = new ArrayList<>();
            while (resultSet.next()) {
                patients.add(map(resultSet));
            }
            return patients;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public String generateNextId() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT patient_id FROM patient WHERE patient_id REGEXP '^PT[0-9]{3}$' ORDER BY patient_id DESC LIMIT 1");
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return "PT001";
            }
            String lastId = resultSet.getString("patient_id");
            return String.format("PT%03d", Integer.parseInt(lastId.substring(2)) + 1);
        } catch (Exception e) {
            throw e;
        }

    }

    private PatientModel map(ResultSet resultSet) throws Exception {
        return new PatientModel(
                resultSet.getString("patient_id"),
                resultSet.getString("patient_name"),
                resultSet.getString("address"),
                resultSet.getString("contact_no"),
                resultSet.getString("nic"),
                resultSet.getDate("date_of_birth").toLocalDate(),
                Gender.valueOf(resultSet.getString("gender")),
                resultSet.getDate("registered_date").toLocalDate(),
                resultSet.getString("email")
        );
    }

}
