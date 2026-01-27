package in.vishal.blooms.dto;

import java.sql.Timestamp;

public class BlogRequest {

    private String blogId;

    private String blogTitle;

    private String blogDescription;

    private String blogContent;

    private String blogStatus;

    private String blogAuthorId;

    private Timestamp blogCreatedDTTM;

    private String blogCategoryId; ;
    private String blogSubcategoryId;

    public BlogRequest(String blogId, String blogTitle, String blogDescription, String blogContent, String blogStatus, String blogAuthorId, Timestamp blogCreatedDTTM, String blogCategoryId, String blogSubcategoryId) {
        this.blogId = blogId;
        this.blogTitle = blogTitle;
        this.blogDescription = blogDescription;
        this.blogContent = blogContent;
        this.blogStatus = blogStatus;
        this.blogAuthorId = blogAuthorId;
        this.blogCreatedDTTM = blogCreatedDTTM;
        this.blogCategoryId = blogCategoryId;
        this.blogSubcategoryId = blogSubcategoryId;
    }





    public BlogRequest() {
    }

    public String getblogSubcategoryId() {
        return blogSubcategoryId;
    }

    public void setblogSubcategoryId(String blogSubcategoryId) {
        this.blogSubcategoryId = blogSubcategoryId;
    }

    public String getblogCategoryId() {
        return blogCategoryId;
    }

    public void setblogCategoryId(String blogCategoryId) {
        this.blogCategoryId = blogCategoryId;
    }

    public Timestamp getblogCreatedDTTM() {
        return blogCreatedDTTM;
    }

    public void setblogCreatedDTTM(Timestamp blogCreatedDTTM) {
        this.blogCreatedDTTM = blogCreatedDTTM;
    }

    public String getblogAuthorId() {
        return blogAuthorId;
    }

    public void setblogAuthorId(String blogAuthorId) {
        this.blogAuthorId = blogAuthorId;
    }

    public String getblogStatus() {
        return blogStatus;
    }

    public void setblogStatus(String blogStatus) {
        this.blogStatus = blogStatus;
    }

    public String getblogContent() {
        return blogContent;
    }

    public void setblogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public String getblogDescription() {
        return blogDescription;
    }

    public void setblogDescription(String blogDescription) {
        this.blogDescription = blogDescription;
    }

    public String getblogTitle() {
        return blogTitle;
    }

    public void setblogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getblogId() {
        return blogId;
    }

    public void setblogId(String blogId) {
        this.blogId = blogId;
    }




}
