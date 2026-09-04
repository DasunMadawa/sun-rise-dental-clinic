package controller;

import com.jfoenix.controls.JFXSpinner;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import model.AppointmentModel;
import model.PatientModel;
import model.PaymentModel;

public class MenuFormController {
    @FXML
    public CategoryAxis monthAxisX;

    @FXML
    public NumberAxis revenueAxisY;

    @FXML
    private Label totalPatientsLbl;

    @FXML
    private Label todayAppointmentsLbl;

    @FXML
    private Label monthRevenueLbl;

    @FXML
    private JFXSpinner paidSpinner;

    @FXML
    private LineChart<?, ?> lineChart;

    @FXML
    public void initialize() {
        try {
            totalPatientsLbl.setText(PatientModel.getTotalCount() + "");
            todayAppointmentsLbl.setText(AppointmentModel.getTodayCount() + "");
            monthRevenueLbl.setText(String.format("Rs. %,.2f", PaymentModel.getMonthRevenue()));
            paidSpinner.setProgress(PaymentModel.getPaidPercentage());

            double[] revenueChart = PaymentModel.getRevenueChart();
            XYChart.Series series = new XYChart.Series();
            for (int i = 0; i < 12; i++) {
                series.getData().add(new XYChart.Data((i + 1) + "", revenueChart[i]));
            }
            lineChart.getData().add(series);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
