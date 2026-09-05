package report.dto;

public class RevenueRow {
    private String month;
    private double revenue;

    public RevenueRow(String month, double revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    public String getMonth() {
        return month;
    }

    public double getRevenue() {
        return revenue;
    }

}
