package in.vishal.blooms.dto;

public class BlogResponse {

    private String blogId;
    private String title;
    private String description;
    private String content;

    private String categoryName;
    private String subCategoryName;

    private String authorId;
private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BlogResponse() {
    }

    public BlogResponse(String blogId, String title, String description, String content, String categoryName, String subCategoryName, String authorId) {
        this.blogId = blogId;
        this.title = title;
        this.description = description;
        this.content = content;
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
        this.authorId = authorId;
    }

    public String getBlogId()
    {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}
