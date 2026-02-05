package in.vishal.blooms.dto;

public class LoginRequest {

    private String phoneNumber;
    private String password;

    // 🔥 DEFAULT CONSTRUCTOR (MANDATORY)
    public LoginRequest() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
