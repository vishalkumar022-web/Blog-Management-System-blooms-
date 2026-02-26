package in.vishal.blooms.service;

import in.vishal.blooms.dto.*;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.*;
import in.vishal.blooms.repository.UserRepository;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    private boolean isValidRole(String role) {
        if (role == null) return false;
        for (Role r : Role.values()) {
            if (r.getDisplayName().equalsIgnoreCase(role)) return true;
        }
        return false;
    }

    public ApiResponse<String> registerUser(UserRequest userRequest) {
        if (!isValidRole(userRequest.getRole())) {
            throw new ApplicationException("Invalid role given. Allowed roles: user, admin");
        }
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new ApplicationException("Email ID already registered.");
        }
        if (userRepository.findByPhoneNumber(userRequest.getPhoneNumber()).isPresent()) {
            throw new ApplicationException("Phone Number already registered.");
        }

        try {
            User user = new User();
            user.setId(UUID.randomUUID().toString()); // ✅ UUID use kiya collision se bachne ke liye
            user.setEmail(userRequest.getEmail());
            user.setName(userRequest.getName());
            user.setUserName(userRequest.getUserName());
            user.setProfileUrl(userRequest.getProfileUrl());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword())); // ✅ Password hash kar diya
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setRole(userRequest.getRole());
            user.setActive(true);

            userRepository.save(user);
            return new ApiResponse<>(true, "User registered successfully.", null);
        } catch (Exception e) {
            throw new ApplicationException("Error saving user: " + e.getMessage());
        }
    }

    public ApiResponse<UserResponse> login(LoginRequest loginRequest) {
        User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
                .orElseThrow(() -> new ApplicationException("Phone number not found"));

        if (!user.isActive()) throw new ApplicationException("User is inactive");

        // ✅ BCrypt check
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ApplicationException("Wrong password");
        }

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
    }

    private Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public ApiResponse<String> sendOtp(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new ApplicationException("Email ID not exist buddy");
        }

        String otp = String.valueOf(new Random().nextInt(9000) + 1000);

        try {
            String url = "https://api.brevo.com/v3/smtp/email";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of("name", "Blooms App", "email", senderEmail));
            body.put("to", List.of(Map.of("email", email)));
            body.put("subject", "Blooms Password Reset OTP");
            body.put("textContent", "Hello,\n\nYour OTP for password reset is: " + otp + "\n\nThanks,\nBlooms Team");

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                otpStorage.put(email, otp);
                return new ApiResponse<>(true, "OTP sent successfully to " + email, null);
            } else {
                throw new ApplicationException("Failed to send email via Brevo.");
            }
        } catch (Exception e) {
            throw new ApplicationException("Failed to send email. Check API Key.");
        }
    }

    public ApiResponse<String> resetPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        String userOtp = request.getOtp();
        String newPassword = request.getNewPassword();

        if (userOtp == null || userOtp.isEmpty()) throw new ApplicationException("Please give otp first");
        if (!otpStorage.containsKey(email)) throw new ApplicationException("Please send OTP first!");
        if (!otpStorage.get(email).equals(userOtp)) throw new ApplicationException("Invalid OTP! Try again.");

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApplicationException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword)); // ✅ New Password ko bhi hash kiya
        userRepository.save(user);

        otpStorage.remove(email);
        return new ApiResponse<>(true, "Password changed successfully", null);
    }
}