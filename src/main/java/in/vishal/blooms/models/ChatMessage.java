package in.vishal.blooms.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

// Ye batata hai ki MongoDB me 'ChatMessages' naam ka ek naya table/collection banega.
@Document(collection = "ChatMessages")
public class ChatMessage {

    // Har message ki ek unique ID hoti hai, isliye @Id lagaya.
    @Id
    private String id;

    // Kisne message bheja (Sender ki User ID)
    private String senderId;

    // Kisko message bheja (Receiver ki User ID)
    private String receiverId;

    // Asli message kya hai (Jaise: "Bhai kya haal hai?")
    private String content;

    // Message kis time par bheja gaya, wo record karne ke liye.
    private LocalDateTime timestamp;

    // Default Constructor (Spring Boot ko object banane ke liye iski zaroorat hoti hai)
    public ChatMessage() {
    }

    // --- Getters aur Setters (Data daalne aur nikalne ke liye) ---

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