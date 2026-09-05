package model;

import model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ReceptionistModel extends UserModel {
    private String staffID;
    private String designation;
    private String contactNo;
    private String staffEmail;

    public ReceptionistModel() {

    }

    public ReceptionistModel(String userID, String username, String passwordHash, boolean isActive, LocalDateTime lastLogin, String email, String staffID, String designation, String contactNo, String staffEmail) {
        super(userID, username, passwordHash, UserRole.RECEPTIONIST, isActive, lastLogin, email);
        this.staffID = staffID;
        this.designation = designation;
        this.contactNo = contactNo;
        this.staffEmail = staffEmail;
    }

    @Override
    public boolean canIssueBill() {
        return true;
    }

    @Override
    public boolean canManagePrices() {
        return false;
    }

    @Override
    public List<String> getMenuOptions() {
        return Arrays.asList("Dashboard", "Register Appointment", "Appointments", "Patients", "Billing", "Reports");
    }

    public static String generateNextStaffId() throws Exception {
        return userDAO.generateNextStaffId();
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }

}
