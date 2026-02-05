package in.vishal.blooms.dto;

import in.vishal.blooms.models.Role;

public class UserResponse {

    private String userId;
    private String userName;
    private String email;
    private String name;
    private String profileUrl;
    private String phoneNumber;

    private Role role;

    private String token;   // 🔥 JWT TOKEN

    public UserResponse() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // 🔥 JWT TOKEN
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
