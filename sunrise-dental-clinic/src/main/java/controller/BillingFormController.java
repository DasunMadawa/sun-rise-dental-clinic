package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import model.AppointmentModel;
import model.PaymentModel;
import model.enums.AppointmentStatus;
import model.enums.PaymentMethod;
import model.enums.PaymentStatus;
import report.ReportService;
import util.mail.MailService;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BillingFormController {

    @FXML
    private JFXTextField appointmentNoTxt;

    @FXML
    private JFXButton findBtn;

    @FXML
    private Label patientNameLbl;

    @FXML
    private Label dentistNameLbl;

    @FXML
    private Label treatmentLbl;

    @FXML
    private Label consultationFeeLbl;

    @FXML
    private Label treatmentCostLbl;

    @FXML
    private JFXTextField discountTxt;

    @FXML
    private Label totalLbl;

    @FXML
    private JFXComboBox<String> paymentMethodComboBox;

    @FXML
    private JFXButton issueBillBtn;

    @FXML
    private JFXButton viewBillBtn;

    @FXML
    private TextArea receiptArea;

    AppointmentModel selectedAppointment;
    PaymentModel lastPayment;

    @FXML
    public void initialize() {
        paymentMethodComboBox.setItems(FXCollections.observableArrayList("CASH", "CARD", "INSURANCE"));
        discountTxt.setText("0");
        discountTxt.setDisable(!DashboardFormController.currentUser.canManagePrices());
        issueBillBtn.setVisible(false);
        viewBillBtn.setVisible(false);
    }

    @FXML
    void findBtnOnAction(ActionEvent event) {
        try {
            if (appointmentNoTxt.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Enter an appointment number").show();
                return;
            }

            selectedAppointment = AppointmentModel.search(appointmentNoTxt.getText());
            if (selectedAppointment == null) {
                new Alert(Alert.AlertType.ERROR, "No appointment found with number " + appointmentNoTxt.getText() + ".").show();
                return;
            }

            PaymentModel existing = PaymentModel.searchByAppointmentNo(selectedAppointment.getAppointmentNo());
            if (existing != null) {
                new Alert(Alert.AlertType.ERROR, "This appointment has already been billed (" + existing.getPaymentID() + ").").show();
                issueBillBtn.setVisible(false);
                lastPayment = existing;
                viewBillBtn.setVisible(true);
                return;
            }

            patientNameLbl.setText(selectedAppointment.getPatient().getPatientName());
            dentistNameLbl.setText(selectedAppointment.getDentist().getDentistName());
            treatmentLbl.setText(selectedAppointment.getTreatment().getName() + " x " + selectedAppointment.getNoTooth());
            consultationFeeLbl.setText(String.format("Rs. %,.2f", selectedAppointment.getConsultationFee()));
            treatmentCostLbl.setText(String.format("Rs. %,.2f", selectedAppointment.getTreatmentCost()));
            issueBillBtn.setVisible(true);
            viewBillBtn.setVisible(false);
            recalculateTotal();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Search Failed !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void discountTxtOnKeyReleased() {
        recalculateTotal();
    }

    private void recalculateTotal() {
        if (selectedAppointment == null) {
            return;
        }
        try {
            double discount = discountTxt.getText().isEmpty() ? 0 : Double.parseDouble(discountTxt.getText());
            totalLbl.setText(String.format("Rs. %,.2f", buildPayment(null, discount).getTotalAmount()));
        } catch (NumberFormatException e) {
            totalLbl.setText("-");
        }

    }

    private PaymentModel buildPayment(String paymentId, double discount) {
        return new PaymentModel(
                paymentId, selectedAppointment.getAppointmentNo(), selectedAppointment.getConsultationFee(),
                selectedAppointment.getTreatment().getUnitCost(), selectedAppointment.getNoTooth(),
                selectedAppointment.getTreatmentCost(), discount,
                null, PaymentStatus.PAID, LocalDate.now()
        );
    }

    @FXML
    void issueBillBtnOnAction(ActionEvent event) {
        try {
            if (paymentMethodComboBox.getValue() == null) {
                new Alert(Alert.AlertType.ERROR, "Select a payment method").show();
                return;
            }

            double discount = discountTxt.getText().isEmpty() ? 0 : Double.parseDouble(discountTxt.getText());
            PaymentModel payment = buildPayment(PaymentModel.generateNextId(), discount);
            payment.setPaymentMethod(PaymentMethod.valueOf(paymentMethodComboBox.getValue()));

            payment.save();

            selectedAppointment.setStatus(AppointmentStatus.COMPLETED);
            selectedAppointment.update();

            lastPayment = payment;
            receiptArea.setText(buildReceipt(payment));
            emailReceipt(payment);

            new Alert(Alert.AlertType.INFORMATION, "Bill " + payment.getPaymentID() + " issued. Total: Rs. " + String.format("%,.2f", payment.getTotalAmount()), ButtonType.OK).show();

            issueBillBtn.setVisible(false);
            viewBillBtn.setVisible(true);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Billing Failed !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void viewBillBtnOnAction(ActionEvent event) {
        try {
            ReportService.view(ReportService.fill("/reports/bill_report.jrxml", buildReportParams(lastPayment)));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could Not Open Report !").show();
            e.printStackTrace();
        }

    }

    private Map<String, Object> buildReportParams(PaymentModel payment) {
        Map<String, Object> params = new HashMap<>();
        params.put("paymentId", payment.getPaymentID());
        params.put("appointmentNo", payment.getAppointmentNo());
        params.put("paymentDate", payment.getPaymentDate().toString());
        params.put("patientId", selectedAppointment.getPatient().getPatientID());
        params.put("patientName", selectedAppointment.getPatient().getPatientName());
        params.put("dentistName", selectedAppointment.getDentist().getDentistName());
        params.put("treatmentName", selectedAppointment.getTreatment().getName());
        params.put("noTooth", payment.getNoToothBilled());
        params.put("consultationFee", payment.getConsultationFee());
        params.put("treatmentCost", payment.getTreatmentCost());
        params.put("discount", payment.getDiscount());
        params.put("total", payment.getTotalAmount());
        params.put("paymentMethod", payment.getPaymentMethod().name());
        return params;
    }

    private void emailReceipt(PaymentModel payment) {
        String patientEmail = selectedAppointment.getPatient().getEmail();
        if (patientEmail == null || patientEmail.isBlank()) {
            return;
        }

        try {
            File pdf = ReportService.exportToPdf(ReportService.fill("/reports/bill_report.jrxml", buildReportParams(payment)), payment.getPaymentID() + ".pdf");
            MailService.sendWithAttachmentAsync(
                    patientEmail,
                    "Your Receipt - " + payment.getPaymentID(),
                    "Dear " + selectedAppointment.getPatient().getPatientName() + ",\n\n" +
                            "Thank you for visiting Sunrise Dental Clinic. Your receipt is attached.\n\n" +
                            "Total Paid: Rs. " + String.format("%,.2f", payment.getTotalAmount()) + "\n\n" +
                            "Sunrise Dental Clinic",
                    pdf
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String buildReceipt(PaymentModel payment) {
        StringBuilder sb = new StringBuilder();
        sb.append("SUNRISE DENTAL CLINIC - RECEIPT\n");
        sb.append("Payment No : ").append(payment.getPaymentID()).append("\n");
        sb.append("Appointment: ").append(payment.getAppointmentNo()).append("\n");
        sb.append("Patient    : ").append(selectedAppointment.getPatient().getPatientName()).append("\n");
        sb.append("Dentist    : ").append(selectedAppointment.getDentist().getDentistName()).append("\n");
        sb.append("--------------------------------\n");
        sb.append(selectedAppointment.getTreatment().getName()).append(" x ").append(payment.getNoToothBilled())
                .append(" @ Rs. ").append(String.format("%,.2f", payment.getUnitCostCharged())).append("\n");
        sb.append("Consultation Fee : Rs. ").append(String.format("%,.2f", payment.getConsultationFee())).append("\n");
        sb.append("Treatment Cost   : Rs. ").append(String.format("%,.2f", payment.getTreatmentCost())).append("\n");
        sb.append("Discount         : Rs. ").append(String.format("%,.2f", payment.getDiscount())).append("\n");
        sb.append("--------------------------------\n");
        sb.append("TOTAL            : Rs. ").append(String.format("%,.2f", payment.getTotalAmount())).append("\n");
        return sb.toString();
    }

}
