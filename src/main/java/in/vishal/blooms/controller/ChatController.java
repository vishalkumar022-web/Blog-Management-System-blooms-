package in.vishal.blooms.controller;

import in.vishal.blooms.dto.ChatMessageDto;
import in.vishal.blooms.models.ChatMessage;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController API requests handle karta hai
@RestController
public class ChatController {

    // Receptionist ne Manager (ChatService) ko bula liya
    @Autowired
    private ChatService chatService;

    // ==========================================
    // 1. REAL-TIME SEND MESSAGE (WebSocket API)
    // ==========================================
    // Jab frontend se WebSocket ke through naya message aayega, toh wo yahan aayega.
    @MessageMapping("/chat")
    public void receiveMessage(@Payload ChatMessageDto chatMessageDto) {

        // Receptionist ne seedha Manager (Service) ko kaam de diya
        chatService.processAndSendMessage(chatMessageDto);
    }

    // ==========================================
    // 2. GET CHAT HISTORY (Normal REST API)
    // ==========================================
    // Jab user chat page open karega, toh purane message mangwane ke liye ye call hoga.
    @GetMapping("/api/chat/history")
    public ApiResponse<List<ChatMessage>> getHistory(
            @RequestParam String senderId,
            @RequestParam String receiverId) {

        // Manager se history mangwa kar Frontend ko de di
        return chatService.getChatHistory(senderId, receiverId);
    }
}