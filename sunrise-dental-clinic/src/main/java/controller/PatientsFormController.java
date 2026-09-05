package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AppointmentModel;
import model.PatientModel;
import model.enums.Gender;
import model.tm.PatientTM;
import util.DatePickers;
import util.Validations;

import java.util.List;
import java.util.Optional;

public class PatientsFormController {

    @FXML
    public ToggleGroup gender;

    @FXML
    public JFXRadioButton maleRBtn;

    @FXML
    public JFXRadioButton femaleRBtn;

    @FXML
    public JFXRadioButton otherRBtn;

    @FXML
    private TableView<PatientTM> table;

    @FXML
    private TableColumn<?, ?> patientIdCol;

    @FXML
    private TableColumn<?, ?> nameCol;

    @FXML
    private TableColumn<?, ?> contactCol;

    @FXML
    private TableColumn<?, ?> ageCol;

    @FXML
    private TableColumn<?, ?> genderCol;

    @FXML
    private TableColumn<?, ?> appointmentNoCol;

    @FXML
    private TableColumn<?, ?> dentistCol;

    @FXML
    private TableColumn<?, ?> treatmentCol;

    @FXML
    private TableColumn<?, ?> dateCol;

    @FXML
    private TableColumn<?, ?> statusCol;

    @FXML
    private JFXTextField searchTxt;

    @FXML
    private JFXTextField patientIdTxt;

    @FXML
    private JFXTextField nameTxt;

    @FXML
    private JFXTextField addressTxt;

    @FXML
    private JFXTextField contactTxt;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private JFXTextField emailTxt;

    @FXML
    private JFXButton searchBtn;

    @FXML
    private JFXButton updateBtn;

    @FXML
    private JFXButton deleteBtn;

    @FXML
    private JFXButton clearBtn;

    PatientModel selectedPatient;

    @FXML
    public void initialize() {
        DatePickers.applyFormat(dobPicker);
        setCellValueFactory();
        loadTableValues();
        setBtnsVisible(false);
        patientIdTxt.setEditable(false);
        setTextFieldValidations();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (newRow != null) {
                loadPatient(newRow.getPatientId());
            }
        });
    }

    private void setTextFieldValidations() {
        Validations.setFocus(nameTxt, Validations.namePattern);
        Validations.setFocus(addressTxt, Validations.namePattern);
        Validations.setFocus(contactTxt, Validations.mobilePattern);
        Validations.setFocus(searchTxt, Validations.patientPattern);
    }

    private void setCellValueFactory() {
        patientIdCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        appointmentNoCol.setCellValueFactory(new PropertyValueFactory<>("appointmentNo"));
        dentistCol.setCellValueFactory(new PropertyValueFactory<>("dentistName"));
        treatmentCol.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadTableValues() {
        ObservableList<PatientTM> obList = FXCollections.observableArrayList();
        try {
            List<PatientModel> patients = PatientModel.getAll();
            for (PatientModel patient : patients) {
                AppointmentModel latest = AppointmentModel.getLatestForPatient(patient.getPatientID());
                obList.add(new PatientTM(
                        patient.getPatientID(), patient.getPatientName(), patient.getContactNo(), patient.getAge(), patient.getGender().name(),
                        latest == null ? "" : latest.getAppointmentNo(),
                        latest == null ? "" : latest.getDentist().getDentistName(),
                        latest == null ? "" : latest.getTreatment().getName(),
                        latest == null ? null : latest.getAppointmentDate(),
                        latest == null ? "" : latest.getStatus().name()
                ));
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Data Loading Error !").show();
            e.printStackTrace();
        }
        table.setItems(obList);
    }

    @FXML
    void searchBtnOnAction(ActionEvent event) {
        if (searchTxt.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Enter valid Patient Id like 'PT001'", ButtonType.OK).show();
            return;
        }
        loadPatient(searchTxt.getText());
    }

    private void loadPatient(String patientId) {
        try {
            selectedPatient = PatientModel.search(patientId);
            if (selectedPatient == null) {
                new Alert(Alert.AlertType.ERROR, "Data Missing On This Id !", ButtonType.OK).show();
                return;
            }

            searchTxt.setText(selectedPatient.getPatientID());
            patientIdTxt.setText(selectedPatient.getPatientID());
            nameTxt.setText(selectedPatient.getPatientName());
            addressTxt.setText(selectedPatient.getAddress());
            contactTxt.setText(selectedPatient.getContactNo());
            dobPicker.setValue(selectedPatient.getDateOfBirth());
            emailTxt.setText(selectedPatient.getEmail());

            switch (selectedPatient.getGender()) {
                case MALE: maleRBtn.setSelected(true); break;
                case FEMALE: femaleRBtn.setSelected(true); break;
                default: otherRBtn.setSelected(true);
            }

            setBtnsVisible(true);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Data Missing On This Id !", ButtonType.OK).show();
        }

    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
        try {
            RadioButton genderRBtn = (RadioButton) gender.getSelectedToggle();

            if ((nameTxt.getFocusColor().equals(javafx.scene.paint.Paint.valueOf("red")) || nameTxt.getText().isEmpty()) ||
                    (addressTxt.getFocusColor().equals(javafx.scene.paint.Paint.valueOf("red")) || addressTxt.getText().isEmpty()) ||
                    (contactTxt.getFocusColor().equals(javafx.scene.paint.Paint.valueOf("red")) || contactTxt.getText().isEmpty()) ||
                    dobPicker.getValue() == null ||
                    genderRBtn == null
            ) {
                new Alert(Alert.AlertType.ERROR, "Enter valid Data").show();
                return;
            }

            selectedPatient.setPatientName(nameTxt.getText());
            selectedPatient.setAddress(addressTxt.getText());
            selectedPatient.setContactNo(contactTxt.getText());
            selectedPatient.setDateOfBirth(dobPicker.getValue());
            selectedPatient.setGender(Gender.valueOf(genderRBtn.getText().toUpperCase()));
            selectedPatient.setEmail(emailTxt.getText());

            selectedPatient.update();
            new Alert(Alert.AlertType.INFORMATION, "Patient Updated !").show();
            clearTxtFields();
            loadTableValues();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Patient Not Updated !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete patient " + patientIdTxt.getText() + "? This cannot be undone.", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isEmpty() || choice.get() != ButtonType.YES) {
            return;
        }

        try {
            PatientModel.delete(patientIdTxt.getText());
            new Alert(Alert.AlertType.INFORMATION, "Patient Deleted !").show();
            clearTxtFields();
            loadTableValues();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Patient Not Deleted !").show();
            e.printStackTrace();
        }

    }

    @FXML
    public void searchTxtOnAction(ActionEvent actionEvent) {
        searchBtnOnAction(actionEvent);
    }

    @FXML
    void clearBtnOnAction(ActionEvent event) {
        selectedPatient = null;
        table.getSelectionModel().clearSelection();
        table.getFocusModel().focus(-1);
        clearTxtFields();
    }

    private void clearTxtFields() {
        patientIdTxt.clear();
        nameTxt.clear();
        addressTxt.clear();
        contactTxt.clear();
        dobPicker.setValue(null);
        emailTxt.clear();
        searchTxt.clear();

        setBtnsVisible(false);
        RadioButton genderRBtn = (RadioButton) gender.getSelectedToggle();
        if (genderRBtn != null) {
            genderRBtn.setSelected(false);
        }

    }

    public void setBtnsVisible(boolean ok) {
        updateBtn.setVisible(ok);
        deleteBtn.setVisible(ok);
    }

}
