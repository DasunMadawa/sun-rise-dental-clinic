package model.tm;

import java.time.LocalDate;

public class PatientTM {
    private String patientId;
    private String name;
    private String contactNo;
    private int age;
    private String gender;
    private String appointmentNo;
    private String dentistName;
    private String treatment;
    private LocalDate appointmentDate;
    private String status;

    public PatientTM() {

    }

    public PatientTM(String patientId, String name, String contactNo, int age, String gender, String appointmentNo, String dentistName, String treatment, LocalDate appointmentDate, String status) {
        this.patientId = patientId;
        this.name = name;
        this.contactNo = contactNo;
        this.age = age;
        this.gender = gender;
        this.appointmentNo = appointmentNo;
        this.dentistName = dentistName;
        this.treatment = treatment;
        this.appointmentDate = appointmentDate;
        this.status = status;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAppointmentNo() {
        return appointmentNo;
    }

    public void setAppointmentNo(String appointmentNo) {
        this.appointmentNo = appointmentNo;
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

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
