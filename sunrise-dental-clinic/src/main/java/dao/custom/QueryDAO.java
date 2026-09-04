package dao.custom;

import dao.SuperDAO;

public interface QueryDAO extends SuperDAO {
    public int getTotalPatients() throws Exception;
    public int getTodayAppointmentCount() throws Exception;
    public double getMonthRevenue() throws Exception;
    public double getPaidPercentage() throws Exception;
    public double[] getRevenueChart() throws Exception;

}
