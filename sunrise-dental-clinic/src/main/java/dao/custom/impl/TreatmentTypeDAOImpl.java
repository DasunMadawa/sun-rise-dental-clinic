package dao.custom.impl;

import dao.custom.TreatmentTypeDAO;
import model.TreatmentTypeModel;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TreatmentTypeDAOImpl implements TreatmentTypeDAO {

    @Override
    public boolean add(TreatmentTypeModel treatmentType) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO treatment_type (treatment_code, treatment_name, unit_cost, is_per_tooth, duration_minutes) VALUES (?, ?, ?, ?, ?)"
            );
            bind(statement, treatmentType);

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public TreatmentTypeModel search(String code) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM treatment_type WHERE treatment_code=?");
            statement.setString(1, code);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? map(resultSet) : null;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public boolean update(TreatmentTypeModel treatmentType) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE treatment_type SET treatment_name=?, unit_cost=?, is_per_tooth=?, duration_minutes=? WHERE treatment_code=?"
            );
            statement.setString(1, treatmentType.getName());
            statement.setDouble(2, treatmentType.getUnitCost());
            statement.setBoolean(3, treatmentType.isPerTooth());
            statement.setInt(4, treatmentType.getDurationMinutes());
            statement.setString(5, treatmentType.getCode());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public boolean delete(String code) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM treatment_type WHERE treatment_code=?");
            statement.setString(1, code);

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public List<TreatmentTypeModel> getAll() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM treatment_type ORDER BY treatment_code");

            List<TreatmentTypeModel> treatmentTypes = new ArrayList<>();
            while (resultSet.next()) {
                treatmentTypes.add(map(resultSet));
            }
            return treatmentTypes;
        } catch (Exception e) {
            throw e;
        }

    }

    private void bind(PreparedStatement statement, TreatmentTypeModel treatmentType) throws Exception {
        statement.setString(1, treatmentType.getCode());
        statement.setString(2, treatmentType.getName());
        statement.setDouble(3, treatmentType.getUnitCost());
        statement.setBoolean(4, treatmentType.isPerTooth());
        statement.setInt(5, treatmentType.getDurationMinutes());
    }

    private TreatmentTypeModel map(ResultSet resultSet) throws Exception {
        return new TreatmentTypeModel(
                resultSet.getString("treatment_code"),
                resultSet.getString("treatment_name"),
                resultSet.getDouble("unit_cost"),
                resultSet.getBoolean("is_per_tooth"),
                resultSet.getInt("duration_minutes")
        );
    }

}
