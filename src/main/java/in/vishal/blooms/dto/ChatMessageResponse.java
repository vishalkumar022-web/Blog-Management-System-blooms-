package in.vishal.blooms.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

// Ye wo DTO hai jo hum Swagger par response mein dikhayenge.
// implements Serializable zaroori hai taaki Redis/Network pe error na aaye.
public class ChatMessageResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;

    public ChatMessageResponse() {
    }

    // --- Getters & Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}