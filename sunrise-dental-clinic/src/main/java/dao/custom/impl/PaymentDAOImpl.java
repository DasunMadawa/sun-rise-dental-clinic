package dao.custom.impl;

import dao.custom.PaymentDAO;
import model.PaymentModel;
import model.enums.PaymentMethod;
import model.enums.PaymentStatus;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {
    private static final String SELECT_ALL = "SELECT * FROM payment";

    @Override
    public boolean add(PaymentModel payment) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO payment (payment_id, appointment_no, consultation_fee, unit_cost_charged, no_tooth_billed, treatment_cost, discount, payment_method, payment_status, payment_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            bindPayment(statement, payment);

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public PaymentModel search(String id) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL + " WHERE payment_id=?");
            statement.setString(1, id);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? map(resultSet) : null;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public PaymentModel searchByAppointmentNo(String appointmentNo) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL + " WHERE appointment_no=?");
            statement.setString(1, appointmentNo);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? map(resultSet) : null;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public boolean update(PaymentModel payment) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE payment SET consultation_fee=?, unit_cost_charged=?, no_tooth_billed=?, treatment_cost=?, discount=?, payment_method=?, payment_status=?, payment_date=? WHERE payment_id=?"
            );
            statement.setDouble(1, payment.getConsultationFee());
            statement.setDouble(2, payment.getUnitCostCharged());
            statement.setInt(3, payment.getNoToothBilled());
            statement.setDouble(4, payment.getTreatmentCost());
            statement.setDouble(5, payment.getDiscount());
            statement.setString(6, payment.getPaymentMethod().name());
            statement.setString(7, payment.getPaymentStatus().name());
            statement.setDate(8, java.sql.Date.valueOf(payment.getPaymentDate()));
            statement.setString(9, payment.getPaymentID());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public boolean delete(String id) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM payment WHERE payment_id=?");
            statement.setString(1, id);

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public List<PaymentModel> getAll() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL + " ORDER BY payment_id");

            List<PaymentModel> payments = new ArrayList<>();
            while (resultSet.next()) {
                payments.add(map(resultSet));
            }
            return payments;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public String generateNextId() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT payment_id FROM payment ORDER BY payment_id DESC LIMIT 1");

        if (!resultSet.next()) {
            return "PAY0001";
        }
        String lastId = resultSet.getString("payment_id");
        return String.format("PAY%04d", Integer.parseInt(lastId.substring(3)) + 1);
    }

    private void bindPayment(PreparedStatement statement, PaymentModel payment) throws Exception {
        statement.setString(1, payment.getPaymentID());
        statement.setString(2, payment.getAppointmentNo());
        statement.setDouble(3, payment.getConsultationFee());
        statement.setDouble(4, payment.getUnitCostCharged());
        statement.setInt(5, payment.getNoToothBilled());
        statement.setDouble(6, payment.getTreatmentCost());
        statement.setDouble(7, payment.getDiscount());
        statement.setString(8, payment.getPaymentMethod().name());
        statement.setString(9, payment.getPaymentStatus().name());
        statement.setDate(10, java.sql.Date.valueOf(payment.getPaymentDate()));
    }

    private PaymentModel map(ResultSet resultSet) throws Exception {
        return new PaymentModel(
                resultSet.getString("payment_id"),
                resultSet.getString("appointment_no"),
                resultSet.getDouble("consultation_fee"),
                resultSet.getDouble("unit_cost_charged"),
                resultSet.getInt("no_tooth_billed"),
                resultSet.getDouble("treatment_cost"),
                resultSet.getDouble("discount"),
                PaymentMethod.valueOf(resultSet.getString("payment_method")),
                PaymentStatus.valueOf(resultSet.getString("payment_status")),
                resultSet.getDate("payment_date").toLocalDate()
        );
    }

}
