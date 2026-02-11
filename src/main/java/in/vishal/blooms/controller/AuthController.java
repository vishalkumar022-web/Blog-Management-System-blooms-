package in.vishal.blooms.controller;

import in.vishal.blooms.dto.LoginRequest;
import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.dto.ForgotPasswordRequest; // ✅ Import Add kiya
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
        ApiResponse<String> response = authService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    // 2. Login User
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(@RequestBody LoginRequest request) {
        ApiResponse<UserResponse> response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // FORGOT PASSWORD APIS (FIXED NAMES)
    // ==========================================

    // API: Send OTP
    @PostMapping("/forgot/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestParam String email) {
        // ✅ FIXED: Method name 'sendOtp' use kiya
        return ResponseEntity.ok(authService.sendOtp(email));
    }

    // API: Reset Password
    @PostMapping("/forgot/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ForgotPasswordRequest request) {
        // ✅ FIXED: Method name 'resetPassword' use kiya
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}