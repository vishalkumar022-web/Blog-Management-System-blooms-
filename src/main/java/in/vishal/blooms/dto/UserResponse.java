package in.vishal.blooms.dto;

import java.io.Serializable; // ✅ IMPORT
import java.util.List;

public class UserResponse implements Serializable { // ✅ IMPLEMENTS SERIALIZABLE

    private static final long serialVersionUID = 1L;

    private String userId;
    private String userName;
    private String email;
    private String name;
    private String profileUrl;
    private String phoneNumber;

    private String role;

    private String token;   // 🔥 JWT TOKEN


    // ✅ NEW FIELDS: User ka contribution dikhane ke liye
    private List<String> myCreatedCategories;
    private List<String> myCreatedSubCategories;
    private List<String> myCreatedBlogs;


    // Baki ka tera code waise hi rahega, bas ye niche add kar de:
    private long followerCount;
    private long followingCount;

    public long getFollowerCount() { return followerCount; }
    public void setFollowerCount(long followerCount) { this.followerCount = followerCount; }

    public long getFollowingCount() { return followingCount; }
    public void setFollowingCount(long followingCount) { this.followingCount = followingCount; }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // 🔥 JWT TOKEN
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // ✅ NEW GETTERS & SETTERS FOR LISTS
    public List<String> getMyCreatedCategories() { return myCreatedCategories; }
    public void setMyCreatedCategories(List<String> myCreatedCategories) { this.myCreatedCategories = myCreatedCategories; }

    public List<String> getMyCreatedSubCategories() { return myCreatedSubCategories; }
    public void setMyCreatedSubCategories(List<String> myCreatedSubCategories) { this.myCreatedSubCategories = myCreatedSubCategories; }

    public List<String> getMyCreatedBlogs() { return myCreatedBlogs; }
    public void setMyCreatedBlogs(List<String> myCreatedBlogs) { this.myCreatedBlogs = myCreatedBlogs; }
}
