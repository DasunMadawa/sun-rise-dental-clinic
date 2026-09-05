package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import model.AppointmentModel;
import model.DentistModel;
import model.PatientModel;
import model.PaymentModel;
import report.ReportService;
import report.dto.AppointmentRow;
import report.dto.PatientRow;
import report.dto.RevenueRow;
import util.DatePickers;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportsFormController {

    @FXML
    private JFXButton patientListBtn;

    @FXML
    private JFXComboBox<String> dentistComboBox;

    @FXML
    private DatePicker scheduleDatePicker;

    @FXML
    private JFXButton scheduleReportBtn;

    @FXML
    private JFXButton revenueReportBtn;

    Map<String, DentistModel> dentistsById = new HashMap<>();

    @FXML
    public void initialize() {
        DatePickers.applyFormat(scheduleDatePicker);
        scheduleDatePicker.setValue(LocalDate.now());

        try {
            ObservableList<String> options = FXCollections.observableArrayList("All Dentists");
            for (DentistModel dentist : DentistModel.getAllActive()) {
                dentistsById.put(dentist.getDentistID(), dentist);
                options.add(dentist.getDentistID() + " - " + dentist.getDentistName());
            }
            dentistComboBox.setItems(options);
            dentistComboBox.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void patientListBtnOnAction(ActionEvent event) {
        try {
            List<PatientRow> rows = new ArrayList<>();
            for (PatientModel patient : PatientModel.getAll()) {
                rows.add(new PatientRow(
                        patient.getPatientID(), patient.getPatientName(), patient.getContactNo(), patient.getNic(),
                        patient.getDateOfBirth().toString(), patient.getAge(), patient.getGender().name(),
                        patient.getRegisteredDate().toString()
                ));
            }

            ReportService.view(ReportService.fill("/reports/patients_report.jrxml", new HashMap<>(), rows));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could Not Open Report !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void scheduleReportBtnOnAction(ActionEvent event) {
        try {
            LocalDate date = scheduleDatePicker.getValue();
            if (date == null) {
                new Alert(Alert.AlertType.ERROR, "Pick a date").show();
                return;
            }
            String dentistSelection = dentistComboBox.getValue();

            List<AppointmentModel> appointments;
            String scopeLabel;
            if (dentistSelection == null || dentistSelection.equals("All Dentists")) {
                appointments = new ArrayList<>();
                for (AppointmentModel appointment : AppointmentModel.getAll()) {
                    if (appointment.getAppointmentDate().equals(date)) {
                        appointments.add(appointment);
                    }
                }
                scopeLabel = "All Dentists - " + date;
            } else {
                String dentistId = dentistSelection.split(" - ")[0];
                appointments = AppointmentModel.getByDentistAndDate(dentistId, date);
                scopeLabel = dentistsById.get(dentistId).getDentistName() + " - " + date;
            }

            List<AppointmentRow> rows = new ArrayList<>();
            for (AppointmentModel appointment : appointments) {
                rows.add(new AppointmentRow(
                        appointment.getAppointmentNo(), appointment.getPatient().getPatientName(), appointment.getDentist().getDentistName(),
                        appointment.getAppointmentDate().toString(), appointment.getAppointmentTime().toString(),
                        appointment.getTreatment().getName(), appointment.getNoTooth(), appointment.getStatus().name()
                ));
            }

            Map<String, Object> params = new HashMap<>();
            params.put("scopeLabel", scopeLabel);

            ReportService.view(ReportService.fill("/reports/appointments_report.jrxml", params, rows));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could Not Open Report !").show();
            e.printStackTrace();
        }

    }

    @FXML
    void revenueReportBtnOnAction(ActionEvent event) {
        try {
            double[] chart = PaymentModel.getRevenueChart();

            List<RevenueRow> rows = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                String monthName = java.time.Month.of(i + 1).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                rows.add(new RevenueRow(monthName, chart[i]));
            }

            Map<String, Object> params = new HashMap<>();
            params.put("year", String.valueOf(Year.now().getValue()));

            ReportService.view(ReportService.fill("/reports/revenue_report.jrxml", params, rows));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could Not Open Report !").show();
            e.printStackTrace();
        }

    }

}
