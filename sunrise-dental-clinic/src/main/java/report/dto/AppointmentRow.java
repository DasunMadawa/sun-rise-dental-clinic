package report.dto;

public class AppointmentRow {
    private String appointmentNo;
    private String patientName;
    private String dentistName;
    private String appointmentDate;
    private String appointmentTime;
    private String treatment;
    private int noTooth;
    private String status;

    public AppointmentRow(String appointmentNo, String patientName, String dentistName, String appointmentDate, String appointmentTime, String treatment, int noTooth, String status) {
        this.appointmentNo = appointmentNo;
        this.patientName = patientName;
        this.dentistName = dentistName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.treatment = treatment;
        this.noTooth = noTooth;
        this.status = status;
    }

    public String getAppointmentNo() {
        return appointmentNo;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDentistName() {
        return dentistName;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public String getTreatment() {
        return treatment;
    }

    public int getNoTooth() {
        return noTooth;
    }

    public String getStatus() {
        return status;
    }

}
