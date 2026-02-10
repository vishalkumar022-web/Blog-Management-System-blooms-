package in.vishal.blooms.dto;

import in.vishal.blooms.models.Role;

public class UserRequest {

    private String userId;
    private String userName;
    private String email;
    private String name;
    private String profileUrl;
    private String password;
    private String phoneNumber;
    private String role ;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public UserRequest(String userId, String phoneNumber,String userName, String email, String name, String profileUrl, String password) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.name = name;
        this.profileUrl = profileUrl;
        this.password = password;
        this.phoneNumber=phoneNumber;
    }

    public UserRequest() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
