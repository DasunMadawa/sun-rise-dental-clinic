package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Paint;
import model.AppointmentModel;
import model.DentistModel;
import model.PatientModel;
import model.ReceptionistModel;
import model.enums.AppointmentStatus;
import model.enums.Gender;
import model.enums.TreatmentType;
import util.Validations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationFormController {

    @FXML
    public JFXRadioButton maleRBtn;

    @FXML
    public JFXRadioButton femaleRBtn;

    @FXML
    public JFXRadioButton otherRBtn;

    @FXML
    public ToggleGroup gender;

    @FXML
    private JFXTextField nicTxt;

    @FXML
    private JFXButton searchBtn;

    @FXML
    private JFXTextField patientIdTxt;

    @FXML
    private JFXTextField nameTxt;

    @FXML
    private JFXTextField addressTxt;

    @FXML
    private JFXTextField contactTxt;

    @FXML
    private JFXTextField dobTxt;

    @FXML
    private JFXTextField appointmentNoTxt;

    @FXML
    private JFXComboBox<String> dentistComboBox;

    @FXML
    private JFXComboBox<String> treatmentComboBox;

    @FXML
    private JFXTextField toothCountTxt;

    @FXML
    private JFXTextField appointmentDateTxt;

    @FXML
    private JFXTextField appointmentTimeTxt;

    @FXML
    private JFXButton registerBtn;

    Map<String, DentistModel> dentistsById = new HashMap<>();
    PatientModel foundPatient;

    @FXML
    public void initialize() {
        init();
        setEditable(false);
        setTextFieldValidations();
    }

    private void init() {
        try {
            appointmentNoTxt.setText(AppointmentModel.generateNextId());
            appointmentDateTxt.setText(LocalDate.now().toString());

            ObservableList<String> dentistOptions = FXCollections.observableArrayList();
            List<DentistModel> dentists = DentistModel.getAllActive();
            for (DentistModel dentist : dentists) {
                dentistsById.put(dentist.getDentistID(), dentist);
                dentistOptions.add(dentist.getDentistID() + " - " + dentist.getDentistName());
            }
            dentistComboBox.setItems(dentistOptions);

            ObservableList<String> treatmentOptions = FXCollections.observableArrayList();
            for (TreatmentType type : TreatmentType.values()) {
                treatmentOptions.add(type.name());
            }
            treatmentComboBox.setItems(treatmentOptions);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setEditable(boolean editable) {
        patientIdTxt.setEditable(false);
    }

    private void setTextFieldValidations() {
        Validations.setFocus(nicTxt, Validations.nicPattern);
        Validations.setFocus(nameTxt, Validations.namePattern);
        Validations.setFocus(addressTxt, Validations.namePattern);
        Validations.setFocus(contactTxt, Validations.mobilePattern);
        Validations.setFocus(dobTxt, Validations.datePattern);
        Validations.setFocus(appointmentTimeTxt, Validations.timePattern);
    }

    @FXML
    void searchBtnOnAction(ActionEvent event) {
        try {
            if (nicTxt.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Enter a NIC to search").show();
                return;
            }

            foundPatient = PatientModel.searchByNic(nicTxt.getText());

            if (foundPatient != null) {
                patientIdTxt.setText(foundPatient.getPatientID());
                nameTxt.setText(foundPatient.getPatientName());
                addressTxt.setText(foundPatient.getAddress());
                contactTxt.setText(foundPatient.getContactNo());
                dobTxt.setText(foundPatient.getDateOfBirth().toString());

                switch (foundPatient.getGender()) {
                    case MALE: maleRBtn.setSelected(true); break;
                    case FEMALE: femaleRBtn.setSelected(true); break;
                    default: otherRBtn.setSelected(true);
                }

                new Alert(Alert.AlertType.INFORMATION, "Found: " + foundPatient.getPatientName() + ", " + foundPatient.getAge() + ". Reusing existing record.").show();
            } else {
                patientIdTxt.setText(PatientModel.generateNextId());
                new Alert(Alert.AlertType.INFORMATION, "No patient found for this NIC. Fill in the details below to register a new patient.").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Search Failed !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void treatmentComboBoxOnAction(ActionEvent event) {
        if (treatmentComboBox.getValue() == null) {
            return;
        }
        TreatmentType type = TreatmentType.valueOf(treatmentComboBox.getValue());
        if (type.isPerTooth()) {
            toothCountTxt.setDisable(false);
            toothCountTxt.clear();
        } else {
            toothCountTxt.setText("1");
            toothCountTxt.setDisable(true);
        }
    }

    @FXML
    void registerBtnOnAction(ActionEvent event) {
        try {
            RadioButton genderRBtn = (RadioButton) gender.getSelectedToggle();

            if (nicTxt.getText().isEmpty() ||
                    (nameTxt.getFocusColor().equals(Paint.valueOf("red")) || nameTxt.getText().isEmpty()) ||
                    (addressTxt.getFocusColor().equals(Paint.valueOf("red")) || addressTxt.getText().isEmpty()) ||
                    (contactTxt.getFocusColor().equals(Paint.valueOf("red")) || contactTxt.getText().isEmpty()) ||
                    (dobTxt.getFocusColor().equals(Paint.valueOf("red")) || dobTxt.getText().isEmpty()) ||
                    genderRBtn == null || dentistComboBox.getValue() == null || treatmentComboBox.getValue() == null ||
                    appointmentTimeTxt.getText().isEmpty() || toothCountTxt.getText().isEmpty()
            ) {
                new Alert(Alert.AlertType.ERROR, "Enter valid Data").show();
                return;
            }

            if (foundPatient == null) {
                PatientModel patient = new PatientModel(
                        patientIdTxt.getText(), nameTxt.getText(), addressTxt.getText(), contactTxt.getText(), nicTxt.getText(),
                        LocalDate.parse(dobTxt.getText()), Gender.valueOf(genderRBtn.getText().toUpperCase()), LocalDate.now()
                );
                patient.save();
                foundPatient = patient;
            }

            String dentistId = dentistComboBox.getValue().split(" - ")[0];
            DentistModel dentist = dentistsById.get(dentistId);
            TreatmentType treatment = TreatmentType.valueOf(treatmentComboBox.getValue());
            LocalDate appointmentDate = LocalDate.parse(appointmentDateTxt.getText());
            LocalTime appointmentTime = LocalTime.parse(appointmentTimeTxt.getText());
            int noTooth = Integer.parseInt(toothCountTxt.getText());

            String bookedBy = DashboardFormController.currentUser instanceof ReceptionistModel
                    ? ((ReceptionistModel) DashboardFormController.currentUser).getStaffID()
                    : DashboardFormController.currentUser.getUserID();

            AppointmentModel appointment = new AppointmentModel(
                    appointmentNoTxt.getText(), foundPatient, dentist, bookedBy, treatment, noTooth,
                    appointmentDate, appointmentTime, AppointmentStatus.SCHEDULED, ""
            );

            List<AppointmentModel> existing = AppointmentModel.getByDentistAndDate(dentistId, appointmentDate);
            for (AppointmentModel other : existing) {
                if (appointment.overlaps(other)) {
                    new Alert(Alert.AlertType.ERROR, dentist.getDentistName() + " is booked " + other.getAppointmentTime() + "-" + other.getEndTime() + ". Next free slot: " + other.getEndTime()).show();
                    appointmentTimeTxt.setText(other.getEndTime().toString());
                    return;
                }
            }

            appointment.save();
            new Alert(Alert.AlertType.INFORMATION, "Appointment " + appointment.getAppointmentNo() + " confirmed: " + appointmentDate + " at " + appointmentTime, ButtonType.OK).show();
            clear();
            init();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Registration Failed !").show();
            e.printStackTrace();
        }

    }

    private void clear() {
        nicTxt.clear();
        patientIdTxt.clear();
        nameTxt.clear();
        addressTxt.clear();
        contactTxt.clear();
        dobTxt.clear();
        toothCountTxt.clear();
        appointmentTimeTxt.clear();
        foundPatient = null;

        RadioButton genderRBtn = (RadioButton) gender.getSelectedToggle();
        if (genderRBtn != null) {
            genderRBtn.setSelected(false);
        }
    }

}
