package in.vishal.blooms.controller;

import in.vishal.blooms.dto.ConnectionResponse;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.security.JwtUtil;
import in.vishal.blooms.service.UserConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController: Ye batata hai ki ye file internet se aane wali request (React se) accept karegi.
// @RequestMapping: Iska matlab ye sari APIs "/api/connection" se start hongi.
@RestController
@RequestMapping("/api/connection")
public class UserConnectionController {

    // JWT Security Guard ko bula liya
    @Autowired
    private JwtUtil jwtUtil;

    // Hamare dimaag/logic (Service) ko bula liya
    @Autowired
    private UserConnectionService connectionService;

    // ==========================================
    // 1. API TO FOLLOW SOMEONE (POST Request)
    // ==========================================
    // @PostMapping ka matlab hum kuch naya bana/save kar rahe hain.
    @PostMapping("/follow")
    public ResponseEntity<ApiResponse<String>> followUser(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam String targetUserId) { // 'targetUserId' URL se aayega

        // Token se "Bearer " hatao
        String token = tokenHeader.substring(7);

        // Token padh kar meri asli ID nikal lo (Taaki main kisi aur ki ID se follow na kar saku)
        String myUserId = jwtUtil.extractUserId(token);

        // Service ko bolo ki Follow karne ka kaam khatam kare
        return ResponseEntity.ok(connectionService.followUser(myUserId, targetUserId));
    }

    // ==========================================
    // 2. API TO UNFOLLOW SOMEONE (POST Request)
    // ==========================================
    @PostMapping("/unfollow")
    public ResponseEntity<ApiResponse<String>> unfollowUser(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam String targetUserId) {

        String token = tokenHeader.substring(7);
        String myUserId = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(connectionService.unfollowUser(myUserId, targetUserId));
    }

    // ==========================================
    // 3. API TO GET FOLLOWERS LIST (GET Request)
    // ==========================================
    // @GetMapping ka matlab hum data "mangwa" rahe hain, save nahi kar rahe.
    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<List<ConnectionResponse>>> getFollowers(@RequestParam String userId) {
        // Ye open rakha hai taaki koi bhi kisi ke bhi followers dekh sake (Jaise Insta me hota hai)
        return ResponseEntity.ok(connectionService.getFollowers(userId));
    }

    // ==========================================
    // 4. API TO GET FOLLOWING LIST (GET Request)
    // ==========================================
    @GetMapping("/following")
    public ResponseEntity<ApiResponse<List<ConnectionResponse>>> getFollowing(@RequestParam String userId) {
        return ResponseEntity.ok(connectionService.getFollowing(userId));
    }
}