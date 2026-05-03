
package in.vishal.blooms.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection = "User")
public class User {
    @Id
    private String id;
    private String userName;
    private String email;
    private String name;
    private String profileUrl;
    private String password;
    private boolean isActive ;
    private String phoneNumber;
    private String role ;

    // User.java file ke andar ke fields jahan khatam ho rahe hain (jaise phone, role)
// wahan ye 2 naye line chipka do:

    private String profileBackgroundUrl; // User ki Profile ke piche lagne wali photo ka link
    private String aboutMe; // User apne bare me jo likhega

    // Phir, iske Getters aur Setters bhi generate kar lena niche.
    public String getProfileBackgroundUrl() { return profileBackgroundUrl; }
    public void setProfileBackgroundUrl(String profileBackgroundUrl) { this.profileBackgroundUrl = profileBackgroundUrl; }

    public String getAboutMe() { return aboutMe; }
    public void setAboutMe(String aboutMe) { this.aboutMe = aboutMe; }


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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
