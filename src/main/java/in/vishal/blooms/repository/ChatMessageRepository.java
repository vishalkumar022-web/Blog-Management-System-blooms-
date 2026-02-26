package in.vishal.blooms.repository;

import in.vishal.blooms.models.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    // Ye custom method MongoDB se un dono dosto ki chat nikalega.
    // Logic: Ya toh (Sender=A aur Receiver=B) ho, YA FIR (Sender=B aur Receiver=A) ho.
    // OrderByTimestampAsc: Taaki purane messages upar aur naye messages niche dikhein (jaise WhatsApp me hota hai).
    List<ChatMessage> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
            String senderId1, String receiverId1,
            String senderId2, String receiverId2
    );
}