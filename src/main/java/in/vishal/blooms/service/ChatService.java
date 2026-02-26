package in.vishal.blooms.service;

import in.vishal.blooms.dto.ChatMessageRequest;
import in.vishal.blooms.dto.ChatMessageResponse;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.ChatMessage;
import in.vishal.blooms.repository.ChatMessageRepository;
import in.vishal.blooms.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 1. Message Send Karne Ka Logic
    public ApiResponse<ChatMessageResponse> sendMessage(String myUserId, ChatMessageRequest request) {
        
        if (request.getTargetUserId() == null || request.getContent() == null) {
            throw new ApplicationException("Target User ID aur Content zaroori hai bhai!");
        }

        // 1. Naya message object banaya DB ke liye
        ChatMessage message = new ChatMessage();
        message.setId(UUID.randomUUID().toString());
        message.setSenderId(myUserId);
        message.setReceiverId(request.getTargetUserId());
        message.setContent(request.getContent());
        message.setTimestamp(LocalDateTime.now());

        // 2. Database mein save kiya
        chatMessageRepository.save(message);

        // 3. Response DTO tayyar kiya Swagger ko dikhane ke liye
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setContent(message.getContent());
        response.setTimestamp(message.getTimestamp());

        // 4. REAL-TIME MAGIC ✨
        // Ye line turant us user ki screen par message bhej degi bina page refresh kiye!
        messagingTemplate.convertAndSend("/topic/messages/" + message.getReceiverId(), response);

        // 5. Swagger ko successfully message return kar do
        return new ApiResponse<>(true, "Message sent successfully", response);
    }

    // 2. Purani Chat History Nikalne Ka Logic
    public ApiResponse<List<ChatMessageResponse>> getChatHistory(String myUserId, String targetUserId) {
        
        // DB se saari baatein nikal li dono dosto ki
        List<ChatMessage> history = chatMessageRepository
                .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
                        myUserId, targetUserId, targetUserId, myUserId);

        // List ko DTO List mein convert karna (Bina kisi extra complex code ke)
        List<ChatMessageResponse> responseList = new ArrayList<>();
        
        for (ChatMessage msg : history) {
            ChatMessageResponse res = new ChatMessageResponse();
            res.setId(msg.getId());
            res.setSenderId(msg.getSenderId());
            res.setReceiverId(msg.getReceiverId());
            res.setContent(msg.getContent());
            res.setTimestamp(msg.getTimestamp());
            
            responseList.add(res);
        }

        return new ApiResponse<>(true, "Chat history fetched successfully", responseList);
    }
}