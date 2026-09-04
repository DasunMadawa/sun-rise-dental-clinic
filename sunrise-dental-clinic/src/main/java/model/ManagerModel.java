package model;

import model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ManagerModel extends UserModel {

    public ManagerModel() {

    }

    public ManagerModel(String userID, String username, String passwordHash, boolean isActive, LocalDateTime lastLogin) {
        super(userID, username, passwordHash, UserRole.MANAGER, isActive, lastLogin);
    }

    @Override
    public boolean canIssueBill() {
        return true;
    }

    @Override
    public boolean canManagePrices() {
        return true;
    }

    @Override
    public List<String> getMenuOptions() {
        return Arrays.asList("Dashboard", "Register Appointment", "Patients", "Billing", "Users");
    }

}
