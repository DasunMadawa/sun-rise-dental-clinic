package model;

import model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ReceptionistModel extends UserModel {
    private String staffID;
    private String designation;
    private String contactNo;
    private String email;

    public ReceptionistModel() {

    }

    public ReceptionistModel(String userID, String username, String passwordHash, boolean isActive, LocalDateTime lastLogin, String staffID, String designation, String contactNo, String email) {
        super(userID, username, passwordHash, UserRole.RECEPTIONIST, isActive, lastLogin);
        this.staffID = staffID;
        this.designation = designation;
        this.contactNo = contactNo;
        this.email = email;
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
        return Arrays.asList("Dashboard", "Register Appointment", "Patients", "Billing");
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
