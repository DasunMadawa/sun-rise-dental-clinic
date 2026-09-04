package dao.custom.impl;

import dao.custom.UserDAO;
import model.DentistModel;
import model.ManagerModel;
import model.ReceptionistModel;
import model.UserModel;
import model.enums.UserRole;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private static final String SELECT_JOIN =
            "SELECT u.*, s.staff_id, s.staff_name, s.designation, s.contact_no AS staff_contact, s.email, " +
                    "d.dentist_id, d.dentist_name, d.specialization, d.contact_no AS dentist_contact, d.consultation_fee, d.available_days " +
                    "FROM user u LEFT JOIN staff s ON s.user_id = u.user_id LEFT JOIN dentist d ON d.user_id = u.user_id";

    @Override
    public boolean add(UserModel user) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO user (user_id, username, password_hash, role, is_active, last_login) VALUES (?, ?, ?, ?, ?, ?)"
            );
            statement.setString(1, user.getUserID());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().name());
            statement.setBoolean(5, user.isActive());
            statement.setTimestamp(6, user.getLastLogin() == null ? null : Timestamp.valueOf(user.getLastLogin()));
            statement.executeUpdate();

            if (user instanceof ReceptionistModel) {
                ReceptionistModel receptionist = (ReceptionistModel) user;
                PreparedStatement staffStatement = connection.prepareStatement(
                        "INSERT INTO staff (staff_id, user_id, staff_name, designation, contact_no, email) VALUES (?, ?, ?, ?, ?, ?)"
                );
                staffStatement.setString(1, receptionist.getStaffID());
                staffStatement.setString(2, receptionist.getUserID());
                staffStatement.setString(3, receptionist.getUsername());
                staffStatement.setString(4, receptionist.getDesignation());
                staffStatement.setString(5, receptionist.getContactNo());
                staffStatement.setString(6, receptionist.getEmail());
                staffStatement.executeUpdate();
            } else if (user instanceof DentistModel) {
                DentistModel dentist = (DentistModel) user;
                PreparedStatement dentistStatement = connection.prepareStatement(
                        "INSERT INTO dentist (dentist_id, user_id, dentist_name, specialization, contact_no, consultation_fee, available_days) VALUES (?, ?, ?, ?, ?, ?, ?)"
                );
                dentistStatement.setString(1, dentist.getDentistID());
                dentistStatement.setString(2, dentist.getUserID());
                dentistStatement.setString(3, dentist.getDentistName());
                dentistStatement.setString(4, dentist.getSpecialization());
                dentistStatement.setString(5, dentist.getContactNo());
                dentistStatement.setDouble(6, dentist.getConsultationFee());
                dentistStatement.setString(7, String.join(",", dentist.getAvailableDays()));
                dentistStatement.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

    }

    @Override
    public UserModel search(String id) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_JOIN + " WHERE u.user_id=? OR u.username=?");
            statement.setString(1, id);
            statement.setString(2, id);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? map(resultSet) : null;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public boolean update(UserModel user) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE user SET username=?, is_active=? WHERE user_id=?"
            );
            statement.setString(1, user.getUsername());
            statement.setBoolean(2, user.isActive());
            statement.setString(3, user.getUserID());
            statement.executeUpdate();

            if (user instanceof ReceptionistModel) {
                ReceptionistModel receptionist = (ReceptionistModel) user;
                PreparedStatement staffStatement = connection.prepareStatement(
                        "UPDATE staff SET staff_name=?, designation=?, contact_no=?, email=? WHERE user_id=?"
                );
                staffStatement.setString(1, receptionist.getUsername());
                staffStatement.setString(2, receptionist.getDesignation());
                staffStatement.setString(3, receptionist.getContactNo());
                staffStatement.setString(4, receptionist.getEmail());
                staffStatement.setString(5, receptionist.getUserID());
                staffStatement.executeUpdate();
            } else if (user instanceof DentistModel) {
                DentistModel dentist = (DentistModel) user;
                PreparedStatement dentistStatement = connection.prepareStatement(
                        "UPDATE dentist SET dentist_name=?, specialization=?, contact_no=?, consultation_fee=?, available_days=? WHERE user_id=?"
                );
                dentistStatement.setString(1, dentist.getDentistName());
                dentistStatement.setString(2, dentist.getSpecialization());
                dentistStatement.setString(3, dentist.getContactNo());
                dentistStatement.setDouble(4, dentist.getConsultationFee());
                dentistStatement.setString(5, String.join(",", dentist.getAvailableDays()));
                dentistStatement.setString(6, dentist.getUserID());
                dentistStatement.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

    }

    @Override
    public boolean delete(String id) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement deleteStaff = connection.prepareStatement("DELETE FROM staff WHERE user_id=?");
            deleteStaff.setString(1, id);
            deleteStaff.executeUpdate();

            PreparedStatement deleteDentist = connection.prepareStatement("DELETE FROM dentist WHERE user_id=?");
            deleteDentist.setString(1, id);
            deleteDentist.executeUpdate();

            PreparedStatement statement = connection.prepareStatement("DELETE FROM user WHERE user_id=?");
            statement.setString(1, id);
            boolean deleted = statement.executeUpdate() > 0;

            connection.commit();
            return deleted;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

    }

    @Override
    public List<UserModel> getAll() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_JOIN + " ORDER BY u.user_id");

            List<UserModel> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(map(resultSet));
            }
            return users;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public List<DentistModel> getAllDentists() throws Exception {
        List<DentistModel> dentists = new ArrayList<>();
        for (UserModel user : getAll()) {
            if (user instanceof DentistModel && user.isActive()) {
                dentists.add((DentistModel) user);
            }
        }
        return dentists;
    }

    @Override
    public String generateNextUserId() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT user_id FROM user ORDER BY user_id DESC LIMIT 1");

        if (!resultSet.next()) {
            return "USR0001";
        }
        String lastId = resultSet.getString("user_id");
        return String.format("USR%04d", Integer.parseInt(lastId.substring(3)) + 1);
    }

    @Override
    public String generateNextStaffId() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT staff_id FROM staff ORDER BY staff_id DESC LIMIT 1");

        if (!resultSet.next()) {
            return "STF001";
        }
        String lastId = resultSet.getString("staff_id");
        return String.format("STF%03d", Integer.parseInt(lastId.substring(3)) + 1);
    }

    @Override
    public String generateNextDentistId() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT dentist_id FROM dentist ORDER BY dentist_id DESC LIMIT 1");

        if (!resultSet.next()) {
            return "DEN001";
        }
        String lastId = resultSet.getString("dentist_id");
        return String.format("DEN%03d", Integer.parseInt(lastId.substring(3)) + 1);
    }

    private UserModel map(ResultSet resultSet) throws Exception {
        UserRole role = UserRole.valueOf(resultSet.getString("role"));
        Timestamp lastLoginTs = resultSet.getTimestamp("last_login");
        boolean isActive = resultSet.getBoolean("is_active");
        String userId = resultSet.getString("user_id");
        String username = resultSet.getString("username");
        String passwordHash = resultSet.getString("password_hash");

        switch (role) {
            case DENTIST:
                return new DentistModel(
                        userId, username, passwordHash, isActive, lastLoginTs == null ? null : lastLoginTs.toLocalDateTime(),
                        resultSet.getString("dentist_id"),
                        resultSet.getString("dentist_name"),
                        resultSet.getString("specialization"),
                        resultSet.getString("dentist_contact"),
                        resultSet.getDouble("consultation_fee"),
                        Arrays.asList(resultSet.getString("available_days").split(","))
                );
            case MANAGER:
                return new ManagerModel(userId, username, passwordHash, isActive, lastLoginTs == null ? null : lastLoginTs.toLocalDateTime());
            case RECEPTIONIST:
            default:
                return new ReceptionistModel(
                        userId, username, passwordHash, isActive, lastLoginTs == null ? null : lastLoginTs.toLocalDateTime(),
                        resultSet.getString("staff_id"),
                        resultSet.getString("designation"),
                        resultSet.getString("staff_contact"),
                        resultSet.getString("email")
                );
        }
    }

}
