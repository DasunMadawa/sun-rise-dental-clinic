package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AppointmentModel;
import model.DentistModel;
import model.enums.AppointmentStatus;
import model.tm.AppointmentTM;
import util.DatePickers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AppointmentsFormController {

    @FXML
    private TableView<AppointmentTM> table;

    @FXML
    private TableColumn<?, ?> appointmentNoCol;

    @FXML
    private TableColumn<?, ?> patientCol;

    @FXML
    private TableColumn<?, ?> dentistCol;

    @FXML
    private TableColumn<?, ?> treatmentCol;

    @FXML
    private TableColumn<?, ?> toothCol;

    @FXML
    private TableColumn<?, ?> dateCol;

    @FXML
    private TableColumn<?, ?> timeCol;

    @FXML
    private TableColumn<?, ?> statusCol;

    @FXML
    private JFXTextField searchTxt;

    @FXML
    private JFXComboBox<String> statusComboBox;

    @FXML
    private DatePicker filterDatePicker;

    @FXML
    private JFXButton clearFiltersBtn;

    @FXML
    private JFXButton cancelBtn;

    Map<String, AppointmentModel> appointmentsByNo = new HashMap<>();
    FilteredList<AppointmentTM> filteredRows;

    @FXML
    public void initialize() {
        DatePickers.applyFormat(filterDatePicker);
        setCellValueFactory();
        statusComboBox.setItems(FXCollections.observableArrayList("All", "SCHEDULED", "COMPLETED", "CANCELLED", "NO_SHOW"));
        statusComboBox.getSelectionModel().selectFirst();
        cancelBtn.setVisible(false);
        cancelBtn.setManaged(DashboardFormController.currentUser != null && !(DashboardFormController.currentUser instanceof DentistModel));

        loadTableValues();

        searchTxt.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        filterDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            cancelBtn.setVisible(newRow != null && "SCHEDULED".equals(newRow.getStatus())
                    && cancelBtn.isManaged());
        });
    }

    private void setCellValueFactory() {
        appointmentNoCol.setCellValueFactory(new PropertyValueFactory<>("appointmentNo"));
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        dentistCol.setCellValueFactory(new PropertyValueFactory<>("dentistName"));
        treatmentCol.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        toothCol.setCellValueFactory(new PropertyValueFactory<>("noTooth"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadTableValues() {
        ObservableList<AppointmentTM> rows = FXCollections.observableArrayList();
        appointmentsByNo.clear();
        try {
            List<AppointmentModel> appointments = AppointmentModel.getAll();
            for (AppointmentModel appointment : appointments) {
                appointmentsByNo.put(appointment.getAppointmentNo(), appointment);
                rows.add(new AppointmentTM(
                        appointment.getAppointmentNo(), appointment.getPatient().getPatientID(), appointment.getPatient().getPatientName(),
                        appointment.getDentist().getDentistName(), appointment.getTreatment().getName(), appointment.getNoTooth(),
                        appointment.getAppointmentDate(), appointment.getAppointmentTime(), appointment.getStatus().name()
                ));
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Data Loading Error !").show();
            e.printStackTrace();
        }

        filteredRows = new FilteredList<>(rows, tm -> true);
        table.setItems(filteredRows);
        applyFilter();
    }

    private void applyFilter() {
        if (filteredRows == null) {
            return;
        }
        String search = searchTxt.getText() == null ? "" : searchTxt.getText().trim().toLowerCase();
        String status = statusComboBox.getValue();

        filteredRows.setPredicate(row -> {
            if (!search.isEmpty() &&
                    !row.getAppointmentNo().toLowerCase().contains(search) &&
                    !row.getPatientName().toLowerCase().contains(search) &&
                    !row.getDentistName().toLowerCase().contains(search)) {
                return false;
            }
            if (status != null && !status.equals("All") && !status.equals(row.getStatus())) {
                return false;
            }
            if (filterDatePicker.getValue() != null && !filterDatePicker.getValue().equals(row.getAppointmentDate())) {
                return false;
            }
            return true;
        });
    }

    @FXML
    void clearFiltersBtnOnAction(ActionEvent event) {
        searchTxt.clear();
        statusComboBox.getSelectionModel().selectFirst();
        filterDatePicker.setValue(null);
        table.getSelectionModel().clearSelection();
        table.getFocusModel().focus(-1);
    }

    @FXML
    void cancelBtnOnAction(ActionEvent event) {
        AppointmentTM selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Cancel appointment " + selected.getAppointmentNo() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isEmpty() || choice.get() != ButtonType.YES) {
            return;
        }

        try {
            AppointmentModel appointment = appointmentsByNo.get(selected.getAppointmentNo());
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointment.update();
            new Alert(Alert.AlertType.INFORMATION, "Appointment Cancelled !").show();
            loadTableValues();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could Not Cancel Appointment !").show();
            e.printStackTrace();
        }

    }

}
