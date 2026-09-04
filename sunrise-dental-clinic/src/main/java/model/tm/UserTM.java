package model.tm;

public class UserTM {
    private String userId;
    private String username;
    private String role;
    private String detail;
    private String contactNo;
    private boolean active;

    public UserTM() {

    }

    public UserTM(String userId, String username, String role, String detail, String contactNo, boolean active) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.detail = detail;
        this.contactNo = contactNo;
        this.active = active;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
