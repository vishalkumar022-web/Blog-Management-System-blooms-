
package in.vishal.blooms.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "BlogLike")
public class BlogLike {

    @Id
    private String id;

    private String blogId;
    private String userId;
    private LocalDateTime createdDTTM;

    public BlogLike() {
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

    public LocalDateTime getCreatedDTTM() {
        return createdDTTM;
    }

    public void setCreatedDTTM(LocalDateTime createdDTTM) {
        this.createdDTTM = createdDTTM;
    }
}
