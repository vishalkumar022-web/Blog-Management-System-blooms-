package in.vishal.blooms.dto;

import java.util.List;


import java.io.Serializable;

public class BlogResponse implements Serializable { // ✅ Change here
    private static final long serialVersionUID = 1L;

    // ... fields getters setters same

    private String blogId;
    private String title;
    private String description;
    private String content;

    private String categoryName;
    private String subCategoryName;

    private String authorId;
    private String status;

    private long likeCount;
    private List<String> likedByUsers;

    private long commentCount;
    private List<CommentResponse> comments;

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(List<String> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public List<CommentResponse> getComments() {
        return comments;
    }

    public void setComments(List<CommentResponse> comments) {
        this.comments = comments;
    }

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
