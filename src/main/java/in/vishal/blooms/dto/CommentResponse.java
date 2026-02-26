package in.vishal.blooms.dto;

import java.io.Serializable; // ✅ 1. Ye import add kiya

// ✅ 2. Yahan 'implements Serializable' add kiya
public class CommentResponse implements Serializable {

    // ✅ 3. Ye ID zaroori hoti hai Redis ke liye
    private static final long serialVersionUID = 1L;

    private String userName;
    private String commentText;

    public CommentResponse() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}