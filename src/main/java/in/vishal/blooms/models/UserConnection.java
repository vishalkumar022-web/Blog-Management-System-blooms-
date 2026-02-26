// File ka pata (address) batane ke liye
package in.vishal.blooms.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex; // ✅ Naya Import
import java.time.LocalDateTime;

// @Document MongoDB ko batata hai ki "Bhai, database me ek naya table banao jiska naam 'UserConnection' hoga"
// AGAR NAHI HOTA: Toh data save kahan hota? MongoDB isko pehchanta hi nahi.
@Document(collection = "UserConnection")
// ✅ FIXED: Database level pe rok diya taaki koi fast click karke 2 baar follow na kar sake
@CompoundIndex(name = "follower_following_idx", def = "{'followerId': 1, 'followingId': 1}", unique = true)


public class UserConnection {

    // @Id batata hai ki ye is table ki primary key (unique pehchaan) hai.
    @Id
    private String id;

    // FollowerId = "Mera ID" (Jo follow wala button daba raha hai)
    private String followerId;

    // FollowingId = "Samne wale ka ID" (Jiski profile pe jakar maine follow dabaya)
    private String followingId;

    // Kis time par follow kiya, wo time save karne ke liye
    private LocalDateTime createdDTTM;

    // Default Constructor (Spring Boot ko object banane ke liye chahiye hota hai)
    public UserConnection() {}

    // ================= GETTERS & SETTERS =================
    // Ye functions data ko read (get) aur write (set) karne ke kaam aate hain.
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFollowerId() { return followerId; }
    public void setFollowerId(String followerId) { this.followerId = followerId; }

    public String getFollowingId() { return followingId; }
    public void setFollowingId(String followingId) { this.followingId = followingId; }

    public LocalDateTime getCreatedDTTM() { return createdDTTM; }
    public void setCreatedDTTM(LocalDateTime createdDTTM) { this.createdDTTM = createdDTTM; }
}