package dao.custom.impl;

import dao.custom.QueryDAO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class QueryDAOImpl implements QueryDAO {

    @Override
    public int getTotalPatients() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM patient");
        resultSet.next();
        return resultSet.getInt("total");
    }

    @Override
    public int getTodayAppointmentCount() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT COUNT(*) AS total FROM appointment WHERE appointment_date = CURDATE() AND status = 'SCHEDULED'"
        );
        resultSet.next();
        return resultSet.getInt("total");
    }

    @Override
    public double getMonthRevenue() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT COALESCE(SUM(consultation_fee + treatment_cost - discount), 0) AS revenue FROM payment " +
                        "WHERE MONTH(payment_date) = MONTH(CURDATE()) AND YEAR(payment_date) = YEAR(CURDATE())"
        );
        resultSet.next();
        return resultSet.getDouble("revenue");
    }

    @Override
    public double getPaidPercentage() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT " +
                        "(SELECT COUNT(*) FROM payment WHERE payment_status = 'PAID') AS paidCount, " +
                        "(SELECT COUNT(*) FROM appointment) AS totalCount"
        );
        resultSet.next();
        int total = resultSet.getInt("totalCount");
        return total == 0 ? 0 : (double) resultSet.getInt("paidCount") / total;
    }

    @Override
    public double[] getRevenueChart() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT MONTH(payment_date) AS month, SUM(consultation_fee + treatment_cost - discount) AS revenue " +
                        "FROM payment WHERE YEAR(payment_date) = YEAR(CURDATE()) GROUP BY MONTH(payment_date)"
        );

        double[] chart = new double[12];
        while (resultSet.next()) {
            chart[resultSet.getInt("month") - 1] = resultSet.getDouble("revenue");
        }
        return chart;
    }

}
