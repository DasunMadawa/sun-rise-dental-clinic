package model;

import dao.DAOFactory;
import dao.custom.PaymentDAO;
import dao.custom.QueryDAO;
import model.enums.PaymentMethod;
import model.enums.PaymentStatus;

import java.time.LocalDate;

public class PaymentModel {
    private static final PaymentDAO paymentDAO = (PaymentDAO) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOTypes.PAYMENT);
    private static final QueryDAO queryDAO = (QueryDAO) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOTypes.QUERY);

    private String paymentID;
    private String appointmentNo;
    private double consultationFee;
    private double unitCostCharged;
    private int noToothBilled;
    private double treatmentCost;
    private double discount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDate;

    public PaymentModel() {

    }

    public PaymentModel(String paymentID, String appointmentNo, double consultationFee, double unitCostCharged, int noToothBilled, double treatmentCost, double discount, PaymentMethod paymentMethod, PaymentStatus paymentStatus, LocalDate paymentDate) {
        this.paymentID = paymentID;
        this.appointmentNo = appointmentNo;
        this.consultationFee = consultationFee;
        this.unitCostCharged = unitCostCharged;
        this.noToothBilled = noToothBilled;
        this.treatmentCost = treatmentCost;
        this.discount = discount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    public double getTotalAmount() {
        return consultationFee + treatmentCost - discount;
    }

    public static PaymentModel search(String id) throws Exception {
        return paymentDAO.search(id);
    }

    public static PaymentModel searchByAppointmentNo(String appointmentNo) throws Exception {
        return paymentDAO.searchByAppointmentNo(appointmentNo);
    }

    public static String generateNextId() throws Exception {
        return paymentDAO.generateNextId();
    }

    public static double getMonthRevenue() throws Exception {
        return queryDAO.getMonthRevenue();
    }

    public static double getPaidPercentage() throws Exception {
        return queryDAO.getPaidPercentage();
    }

    public static double[] getRevenueChart() throws Exception {
        return queryDAO.getRevenueChart();
    }

    public boolean save() throws Exception {
        return paymentDAO.add(this);
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getAppointmentNo() {
        return appointmentNo;
    }

    public void setAppointmentNo(String appointmentNo) {
        this.appointmentNo = appointmentNo;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public double getUnitCostCharged() {
        return unitCostCharged;
    }

    public void setUnitCostCharged(double unitCostCharged) {
        this.unitCostCharged = unitCostCharged;
    }

    public int getNoToothBilled() {
        return noToothBilled;
    }

    public void setNoToothBilled(int noToothBilled) {
        this.noToothBilled = noToothBilled;
    }

    public double getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(double treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

}
