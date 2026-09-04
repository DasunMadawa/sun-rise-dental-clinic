package model;

import dao.DAOFactory;
import dao.custom.UserDAO;
import model.enums.UserRole;
import util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.List;

public abstract class UserModel {
    protected static final UserDAO userDAO = (UserDAO) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOTypes.USER);

    protected String userID;
    protected String username;
    protected String passwordHash;
    protected UserRole role;
    protected boolean isActive;
    protected LocalDateTime lastLogin;

    public UserModel() {

    }

    public UserModel(String userID, String username, String passwordHash, UserRole role, boolean isActive, LocalDateTime lastLogin) {
        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = isActive;
        this.lastLogin = lastLogin;
    }

    public boolean checkPassword(String plain) {
        return passwordHash != null && passwordHash.equals(PasswordUtil.hash(plain));
    }

    public static UserModel search(String idOrUsername) throws Exception {
        return userDAO.search(idOrUsername);
    }

    public static List<UserModel> getAll() throws Exception {
        return userDAO.getAll();
    }

    public static String generateNextUserId() throws Exception {
        return userDAO.generateNextUserId();
    }

    public static boolean delete(String userId) throws Exception {
        return userDAO.delete(userId);
    }

    public boolean save() throws Exception {
        return userDAO.add(this);
    }

    public boolean update() throws Exception {
        return userDAO.update(this);
    }

    public abstract boolean canIssueBill();

    public abstract boolean canManagePrices();

    public abstract List<String> getMenuOptions();

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

}
