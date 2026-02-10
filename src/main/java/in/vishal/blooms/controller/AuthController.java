package in.vishal.blooms.controller;

import in.vishal.blooms.dto.LoginRequest;
import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 1. Register User
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody UserRequest request) {
        // Service khud ApiResponse return kar raha hai
        ApiResponse<String> response = authService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    // 2. Login User
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(@RequestBody LoginRequest request) {
        // Service call kiya, usne ApiResponse diya
        ApiResponse<UserResponse> response = authService.login(request);

        // Bas usko return kar diya with 200 OK
        return ResponseEntity.ok(response);
    }
}