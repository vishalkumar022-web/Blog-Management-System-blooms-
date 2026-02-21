package in.vishal.blooms.dto;

// Ye ek "Safe Parcel" hai.
// EXPLANATION: Jab koi followers ki list mangega, toh hum asli 'User' object nahi denge kyunki usme password hota hai.
// Hum is safe parcel me sirf zaroori cheezein daal kar bhejenge.
public class ConnectionResponse {

    private String userId;       // User ki ID
    private String name;         // Pura naam (Jaise: Vishal Kumar)
    private String userName;     // Unique naam (Jaise: vishal_007)
    private String profileUrl;   // User ki DP (Photo) ka link

    // Khali constructor
    public ConnectionResponse() {}

    // Getters aur Setters taaki data daal aur nikal sakein
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
}