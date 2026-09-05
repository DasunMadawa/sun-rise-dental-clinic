package model.tm;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentTM {
    private String appointmentNo;
    private String patientId;
    private String patientName;
    private String dentistName;
    private String treatment;
    private int noTooth;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;

    public AppointmentTM() {

    }

    public AppointmentTM(String appointmentNo, String patientId, String patientName, String dentistName, String treatment, int noTooth, LocalDate appointmentDate, LocalTime appointmentTime, String status) {
        this.appointmentNo = appointmentNo;
        this.patientId = patientId;
        this.patientName = patientName;
        this.dentistName = dentistName;
        this.treatment = treatment;
        this.noTooth = noTooth;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public String getAppointmentNo() {
        return appointmentNo;
    }

    public void setAppointmentNo(String appointmentNo) {
        this.appointmentNo = appointmentNo;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public int getNoTooth() {
        return noTooth;
    }

    public void setNoTooth(int noTooth) {
        this.noTooth = noTooth;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
