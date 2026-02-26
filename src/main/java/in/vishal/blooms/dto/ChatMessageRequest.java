package in.vishal.blooms.dto;

// Ye wo DTO hai jo hum Swagger (ya React) se bhejenge jab hume naya message type karna hoga.
public class ChatMessageRequest {

    // Kisko message bhejna hai (Target User)
    private String targetUserId;

    // Kya message bhejna hai
    private String content;

    // Sender ID hum yahan nahi lenge, wo hum JWT Token se nikalenge (Security ke liye)

    public ChatMessageRequest() {
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}