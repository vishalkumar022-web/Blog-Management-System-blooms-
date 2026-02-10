
package in.vishal.blooms.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "BlogComment")
public class BlogComment {

    @Id
    private String id;

    private String blogId;
    private String userId;
    private String commentText;
    private LocalDateTime createdDTTM;

    // getters & setters

    public BlogComment(String id, String blogId, String userId, String commentText, LocalDateTime createdDTTM) {
        this.id = id;
        this.blogId = blogId;
        this.userId = userId;
        this.commentText = commentText;
        this.createdDTTM = createdDTTM;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getCreatedDTTM() {
        return createdDTTM;
    }

    public void setCreatedDTTM(LocalDateTime createdDTTM) {
        this.createdDTTM = createdDTTM;
    }

    public BlogComment() {
    }
}
