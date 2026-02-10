package in.vishal.blooms.controller;

import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.response.ApiResponse; // ✅ Import Added
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

    // 1. Get User By ID (Public)
    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@RequestParam String UserId) {
        // Service ApiResponse de raha hai, hum usko ResponseEntity.ok() me wrap karke bhej rahe hain
        return ResponseEntity.ok(userService.getUserById(UserId));
    }

    // 2. Get All Users (Public) with Pagination Defaults
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getUsers(page, size));
    }

    // 3. Search User By Name (Public) with Pagination Defaults
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page, // ✅ Missing argument fixed
            @RequestParam(defaultValue = "10") int size // ✅ Missing argument fixed
    ) {
        return ResponseEntity.ok(userService.searchUsersByName(name, page, size));
    }

    // 4. Update User (SECURE)
    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UserRequest userRequest,
                                                                @RequestHeader("Authorization") String tokenHeader) {

        String token = tokenHeader.substring(7);
        String userIdFromToken = jwtUtil.extractUserId(token);

        userRequest.setUserId(userIdFromToken);

        return ResponseEntity.ok(userService.updateUser(userRequest));
    }

    // 5. Delete User (SECURE)
    @DeleteMapping
    public ResponseEntity<ApiResponse<Boolean>> deleteUser(@RequestHeader("Authorization") String tokenHeader) {

        String token = tokenHeader.substring(7);
        String userIdFromToken = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(userService.deleteUser(userIdFromToken));
    }
}