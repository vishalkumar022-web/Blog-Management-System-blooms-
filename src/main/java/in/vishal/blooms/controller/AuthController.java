package in.vishal.blooms.controller;

import in.vishal.blooms.dto.LoginRequest;
import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // 🌍 Frontend access allow karne ke liye (React/Postman)
public class AuthController {

    private final AuthService authService;

    // ✅ Best Practice: Constructor Injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ===========================
    // REGISTER API
    // ===========================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest request) {
        try {
            authService.registerUser(request);
            // 201 Created status code return karna standard hai
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } catch (RuntimeException e) {
            // Agar user already exist karta hai, to 400 Bad Request bhejo
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ===========================
    // LOGIN API
    // ===========================
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            UserResponse response = authService.login(request);
            return ResponseEntity.ok(response); // ✅ 200 OK + Token
        } catch (RuntimeException e) {
            // Wrong Password ya User Not Found ke liye 401 Unauthorized
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }
}
