package in.vishal.blooms.service;

import in.vishal.blooms.dto.ChatMessageDto;
import in.vishal.blooms.models.ChatMessage;
import in.vishal.blooms.repository.ChatMessageRepository;
import in.vishal.blooms.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// @Service batata hai ki ye class hamare app ka business logic handle karegi
@Service
public class ChatService {

    // Database me save karne ke liye
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    // Turant dusre user ko message bhejane ke liye (Flash Delivery Boy)
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 1. Message process aur send karne ka kaam
    public void processAndSendMessage(ChatMessageDto chatDto) {

        // Step 1: DTO (Lifafa) se data nikal kar asali Model (ChatMessage) me daalo
        ChatMessage message = new ChatMessage();
        message.setId(UUID.randomUUID().toString()); // Nayi unique ID banayi
        message.setTimestamp(LocalDateTime.now());   // Abhi ka time lagaya
        message.setSenderId(chatDto.getSenderId());
        message.setReceiverId(chatDto.getReceiverId());
        message.setContent(chatDto.getContent());

        // Step 2: History ke liye MongoDB me save kar lo
        chatMessageRepository.save(message);

        // Step 3: Receiver ke "Personal In-box" URL par turant bhej do!
        // URL kaisa dikhega: /topic/messages/123456
        messagingTemplate.convertAndSend("/topic/messages/" + message.getReceiverId(), message);
    }

    // 2. Purani Chat History nikalne ka kaam
    public ApiResponse<List<ChatMessage>> getChatHistory(String senderId, String receiverId) {

        // Repository se un dono ki saari purani baatein utha lo
        List<ChatMessage> history = chatMessageRepository
                .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
                        senderId, receiverId, receiverId, senderId);

        return new ApiResponse<>(true, "Chat history fetched successfully", history);
    }
}