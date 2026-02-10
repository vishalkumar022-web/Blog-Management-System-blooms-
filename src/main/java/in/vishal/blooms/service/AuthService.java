package in.vishal.blooms.service;

import in.vishal.blooms.dto.LoginRequest;
import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.Role;
import in.vishal.blooms.models.User;
import in.vishal.blooms.repository.UserRepository;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.security.JwtUtil;
//import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class AuthService {
    // ✅ Manual Logger Definition
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // ================= HELPER METHOD (VALIDATION) =================
    private boolean isValidRole(String role) {
        if (role == null) return false;
        for (Role r : Role.values()) {
            if (r.getDisplayName().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    // =========================
    // REGISTER USER
    // =========================
    public ApiResponse<String> registerUser(UserRequest userRequest) {
        log.info("Registering user with email: {}", userRequest.getEmail());

        // 1. Role Check
        if (!isValidRole(userRequest.getRole())) {
            throw new ApplicationException("Invalid role given. Allowed roles: user, admin");
        }

        // 2. Duplicate Email Check
        Optional<User> existingUserByEmail = userRepository.findByEmail(userRequest.getEmail());
        if (existingUserByEmail.isPresent()) {
            throw new ApplicationException("Email ID '" + userRequest.getEmail() + "' is already registered.");
        }

        // 3. Duplicate Phone Check
        Optional<User> existingUserByPhone = userRepository.findByPhoneNumber(userRequest.getPhoneNumber());
        if (existingUserByPhone.isPresent()) {
            throw new ApplicationException("Phone Number '" + userRequest.getPhoneNumber() + "' is already registered.");
        }

        try {
            User user = new User();
            user.setId(String.valueOf(System.currentTimeMillis())); // Tumhara logic
            user.setEmail(userRequest.getEmail());
            user.setName(userRequest.getName());
            user.setUserName(userRequest.getUserName());
            user.setProfileUrl(userRequest.getProfileUrl());
            user.setPassword(userRequest.getPassword());
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setRole(userRequest.getRole());
            user.setActive(true);

            userRepository.save(user);
            log.info("User registered successfully: {}", user.getId());

            return new ApiResponse<>(true, "User registered successfully.", null);

        } catch (Exception e) {
            log.error("Error saving user: {}", e.getMessage());
            throw new ApplicationException("User register karte waqt database error aaya: " + e.getMessage());
        }
    }

    // =========================
    // LOGIN USER
    // =========================
    public ApiResponse<UserResponse> login(LoginRequest loginRequest) {
        log.info("Login attempt for phone: {}", loginRequest.getPhoneNumber());

        Optional<User> optionalUser = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber());

        // 1. User Not Found
        if (optionalUser.isEmpty()) {
            throw new ApplicationException("Phone number not found");
        }

        User user = optionalUser.get();

        // 2. Inactive Check
        if (!user.isActive()) {
            throw new ApplicationException("User is inactive");
        }

        // 3. Password Check
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new ApplicationException("Wrong password");
        }

        try {
            // 4. Token Generate
            String token = jwtUtil.generateToken(user.getId(), user.getRole());

            UserResponse response = new UserResponse();
            response.setUserId(user.getId());
            response.setUserName(user.getUserName());
            response.setEmail(user.getEmail());
            response.setName(user.getName());
            response.setPhoneNumber(user.getPhoneNumber());
            response.setProfileUrl(user.getProfileUrl());
            response.setRole(user.getRole());
            response.setToken(token);

            return new ApiResponse<>(true, "Login Successful", response);

        } catch (Exception e) {
            log.error("Token generation error: {}", e.getMessage());
            throw new ApplicationException("Login process failed: " + e.getMessage());
        }
    }
}