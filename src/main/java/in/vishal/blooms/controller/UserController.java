package in.vishal.blooms.controller;

import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.security.JwtUtil;
import in.vishal.blooms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/User")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // =======================================================
    // 1. GET MY PROFILE (Secure "Get Me" API)
    // =======================================================
    // Ab user ko "?UserId=..." bhejne ki zarurat nahi. Bas Token bhejo header me.
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(@RequestHeader("Authorization") String tokenHeader) {

        // 1. Token string me se "Bearer " hataya
        String token = tokenHeader.substring(7);

        // 2. Token ke andar se User ID nikali (Magic 🪄)
        String userIdFromToken = jwtUtil.extractUserId(token);

        // 3. Us ID ka data database se mangwa liya (Jo ab categories aur blogs bhi layega)
        return ResponseEntity.ok(userService.getUserById(userIdFromToken));
    }

    // 2. Search User By Name (Public)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.searchUsersByName(name, page, size));
    }

    // 3. Update User (SECURE)
    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UserRequest userRequest,
                                                                @RequestHeader("Authorization") String tokenHeader) {
        // Token se ID nikal kar request me set kar rahe hain taaki koi dusre ka update na kar sake
        String token = tokenHeader.substring(7);
        String userIdFromToken = jwtUtil.extractUserId(token);

        userRequest.setUserId(userIdFromToken);

        return ResponseEntity.ok(userService.updateUser(userRequest));
    }

    // 4. Delete User (SECURE)
    @DeleteMapping
    public ResponseEntity<ApiResponse<Boolean>> deleteUser(@RequestHeader("Authorization") String tokenHeader) {

        String token = tokenHeader.substring(7);
        String userIdFromToken = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(userService.deleteUser(userIdFromToken));
    }
}