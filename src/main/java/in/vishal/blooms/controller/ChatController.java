package in.vishal.blooms.controller;

import in.vishal.blooms.dto.ChatMessageRequest;
import in.vishal.blooms.dto.ChatMessageResponse;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.security.JwtUtil;
import in.vishal.blooms.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private JwtUtil jwtUtil;

    // ==========================================
    // 1. SEND MESSAGE API (POST)
    // ==========================================
    // Swagger se message bhejne ke liye. Ye aage jaake WebSocket pe khud broadcast ho jayega.
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody ChatMessageRequest request) {

        // Token se "Bearer " hataya aur meri (sender) ID nikali
        String token = tokenHeader.substring(7);
        String myUserId = jwtUtil.extractUserId(token);

        // Service ko bola "Bhai message send kar do"
        return ResponseEntity.ok(chatService.sendMessage(myUserId, request));
    }

    // ==========================================
    // 2. GET CHAT HISTORY API (GET)
    // ==========================================
    // Swagger pe kisi user ke sath purani chat dekhne ke liye
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getHistory(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam String targetUserId) {

        // Meri ID token se nikali
        String token = tokenHeader.substring(7);
        String myUserId = jwtUtil.extractUserId(token);

        // Service se history mangwa li
        return ResponseEntity.ok(chatService.getChatHistory(myUserId, targetUserId));
    }
}