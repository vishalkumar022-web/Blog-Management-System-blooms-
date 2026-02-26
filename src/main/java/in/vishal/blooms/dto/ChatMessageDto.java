package in.vishal.blooms.dto;

// Ye hamara 'Lifafa' (DTO) hai. React se frontend sirf ye 3 chizein bhejega.
// ID aur Time hum backend (Service) me khud lagayenge (Security ke liye).
public class ChatMessageDto {

    private String senderId;   // Kisne bheja
    private String receiverId; // Kisko bheja
    private String content;    // Kya message bheja

    // Default Constructor
    public ChatMessageDto() {
    }

    // Getters aur Setters
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}