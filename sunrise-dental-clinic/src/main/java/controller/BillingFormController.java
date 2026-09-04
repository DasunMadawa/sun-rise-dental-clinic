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

import java.time.LocalDate;

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
    private TextArea receiptArea;

    AppointmentModel selectedAppointment;

    @FXML
    public void initialize() {
        paymentMethodComboBox.setItems(FXCollections.observableArrayList("CASH", "CARD", "INSURANCE"));
        discountTxt.setText("0");
        discountTxt.setDisable(!DashboardFormController.currentUser.canManagePrices());
        issueBillBtn.setVisible(false);
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
                return;
            }

            patientNameLbl.setText(selectedAppointment.getPatient().getPatientName());
            dentistNameLbl.setText(selectedAppointment.getDentist().getDentistName());
            treatmentLbl.setText(selectedAppointment.getTreatment().name() + " x " + selectedAppointment.getNoTooth());
            consultationFeeLbl.setText(String.format("Rs. %,.2f", selectedAppointment.getConsultationFee()));
            treatmentCostLbl.setText(String.format("Rs. %,.2f", selectedAppointment.getTreatmentCost()));
            issueBillBtn.setVisible(true);
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

            receiptArea.setText(buildReceipt(payment));
            new Alert(Alert.AlertType.INFORMATION, "Bill " + payment.getPaymentID() + " issued. Total: Rs. " + String.format("%,.2f", payment.getTotalAmount()), ButtonType.OK).show();

            issueBillBtn.setVisible(false);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Billing Failed !").show();
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
        sb.append(selectedAppointment.getTreatment().name()).append(" x ").append(payment.getNoToothBilled())
                .append(" @ Rs. ").append(String.format("%,.2f", payment.getUnitCostCharged())).append("\n");
        sb.append("Consultation Fee : Rs. ").append(String.format("%,.2f", payment.getConsultationFee())).append("\n");
        sb.append("Treatment Cost   : Rs. ").append(String.format("%,.2f", payment.getTreatmentCost())).append("\n");
        sb.append("Discount         : Rs. ").append(String.format("%,.2f", payment.getDiscount())).append("\n");
        sb.append("--------------------------------\n");
        sb.append("TOTAL            : Rs. ").append(String.format("%,.2f", payment.getTotalAmount())).append("\n");
        return sb.toString();
    }

}
