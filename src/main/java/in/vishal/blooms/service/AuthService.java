package in.vishal.blooms.service;

import in.vishal.blooms.dto.LoginRequest;
import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.models.Role;
import in.vishal.blooms.models.User;
import in.vishal.blooms.repository.UserRepository;
import in.vishal.blooms.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // ================= HELPER METHOD (VALIDATION) =================
    // Ye check karega ki role "user" ya "admin" hi ho, aur kuch nahi.
    private boolean isValidRole(String role) {
        if (role == null) return false;

        for (Role r : Role.values()) {
            // Agar incoming role (e.g. "admin") match karta hai Enum display name se
            if (r.getDisplayName().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    // =========================
    // REGISTER USER
    // =========================
    public void registerUser(UserRequest userRequest) {

        // 1️⃣ Validation Check: Role sahi hai ya nahi?
        if (!isValidRole(userRequest.getRole())) {
            // Agar role galat hai toh yahin rok do aur error do

            System.out.println("Invalid role given. Allowed roles: user, admin");

            throw new RuntimeException("Invalid role given. Allowed roles: user, admin");
        }

        User user = new User();
        user.setId(String.valueOf(System.currentTimeMillis()));
        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        user.setUserName(userRequest.getUserName());
        user.setProfileUrl(userRequest.getProfileUrl());
        user.setPassword(userRequest.getPassword());
        user.setPhoneNumber(userRequest.getPhoneNumber());

        // Ab hum sure hain ki role sahi hai, toh set kar do
        user.setRole(userRequest.getRole());

        user.setActive(true);

        userRepository.save(user);
    }
    // =========================
    // LOGIN USER + JWT TOKEN
    // =========================
    public UserResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber());

        // 1️⃣ user nahi mila
        if (user == null) {
            throw new RuntimeException("Phone number not found");
        }

        // 2️⃣ inactive user
        if (!user.isActive()) {
            throw new RuntimeException("User is inactive");
        }

        // 3️⃣ wrong password
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("Wrong password");
        }


        // 4️⃣ token generate
        String token = jwtUtil.generateToken(
                user.getId(),
                user.getRole()
        );

        // 5️⃣ response
        UserResponse response = new UserResponse();

        response.setUserId(user.getId());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setName(user.getName());                 // ✅ ADD
        response.setPhoneNumber(user.getPhoneNumber());   // ✅ ADD
        response.setProfileUrl(user.getProfileUrl());     // ✅ ADD
        response.setRole(user.getRole());
        response.setToken(token);

        return response;

    }



}

