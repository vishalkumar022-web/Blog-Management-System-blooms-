package in.vishal.blooms.dto;

import java.time.LocalDateTime;

public class BlogRequest {
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String blogId;
    private String blogTitle;
    private String blogDescription;
    private String blogContent;
    private String blogStatus;
    private String blogAuthorId;
    private LocalDateTime blogCreatedDTTM;
    private String blogCategoryId;
    private String blogSubcategoryId;
private String userId; // Add userId field
    // Default Constructor
    public BlogRequest() {
    }

    // Parameterized Constructor
    public BlogRequest(String blogId, String blogTitle, String blogDescription, String blogContent,
                       String blogStatus, String blogAuthorId, LocalDateTime blogCreatedDTTM,
                       String blogCategoryId, String blogSubcategoryId , String userId) {
        this.blogId = blogId;
        this.blogTitle = blogTitle;
        this.blogDescription = blogDescription;
        this.blogContent = blogContent;
        this.blogStatus = blogStatus;
        this.blogAuthorId = blogAuthorId;
        this.blogCreatedDTTM = blogCreatedDTTM;
        this.blogCategoryId = blogCategoryId;
        this.blogSubcategoryId = blogSubcategoryId;
        this.userId = userId; // Initialize userId
    }

    // --- Corrected Getters and Setters (Standard Naming) ---

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getBlogDescription() {
        return blogDescription;
    }

    public void setBlogDescription(String blogDescription) {
        this.blogDescription = blogDescription;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public String getBlogStatus() {
        return blogStatus;
    }

    public void setBlogStatus(String blogStatus) {
        this.blogStatus = blogStatus;
    }

    public String getBlogAuthorId() {
        return blogAuthorId;
    }

    public void setBlogAuthorId(String blogAuthorId) {
        this.blogAuthorId = blogAuthorId;
    }

    public LocalDateTime getBlogCreatedDTTM() {
        return blogCreatedDTTM;
    }

    public void setBlogCreatedDTTM(LocalDateTime blogCreatedDTTM) {
        this.blogCreatedDTTM = blogCreatedDTTM;
    }

    public String getBlogCategoryId() {
        return blogCategoryId;
    }

    public void setBlogCategoryId(String blogCategoryId) {
        this.blogCategoryId = blogCategoryId;
    }

    public String getBlogSubcategoryId() {
        return blogSubcategoryId;
    }

    public void setBlogSubcategoryId(String blogSubcategoryId) {
        this.blogSubcategoryId = blogSubcategoryId;
    }
}