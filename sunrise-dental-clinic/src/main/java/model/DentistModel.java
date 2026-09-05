package model;

import model.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DentistModel extends UserModel {
    private String dentistID;
    private String dentistName;
    private String specialization;
    private String contactNo;
    private double consultationFee;
    private List<String> availableDays;

    public DentistModel() {

    }

    public DentistModel(String userID, String username, String passwordHash, boolean isActive, LocalDateTime lastLogin, String email, String dentistID, String dentistName, String specialization, String contactNo, double consultationFee, List<String> availableDays) {
        super(userID, username, passwordHash, UserRole.DENTIST, isActive, lastLogin, email);
        this.dentistID = dentistID;
        this.dentistName = dentistName;
        this.specialization = specialization;
        this.contactNo = contactNo;
        this.consultationFee = consultationFee;
        this.availableDays = availableDays;
    }

    public boolean isAvailableOn(LocalDate d) {
        if (availableDays == null || availableDays.isEmpty()) {
            return true;
        }
        String dayName = d.getDayOfWeek().toString();
        return availableDays.stream().anyMatch(day -> day.equalsIgnoreCase(dayName));
    }

    public static List<DentistModel> getAllActive() throws Exception {
        return userDAO.getAllDentists();
    }

    public static String generateNextDentistId() throws Exception {
        return userDAO.generateNextDentistId();
    }

    @Override
    public boolean canIssueBill() {
        return false;
    }

    @Override
    public boolean canManagePrices() {
        return false;
    }

    @Override
    public List<String> getMenuOptions() {
        return Arrays.asList("Dashboard", "Appointments", "Patients");
    }

    public String getDentistID() {
        return dentistID;
    }

    public void setDentistID(String dentistID) {
        this.dentistID = dentistID;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public List<String> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = availableDays;
    }

}
