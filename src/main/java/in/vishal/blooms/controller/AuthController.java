package in.vishal.blooms.controller;

import in.vishal.blooms.dto.LoginRequest;
import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")   // 🔥 lowercase best practice
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public void registerUser(@RequestBody UserRequest request) {
        authService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            UserResponse response = authService.login(request);
            return ResponseEntity.ok(response); // ✅ 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // ❌ 401
                    .body(e.getMessage());
        }
    }
}