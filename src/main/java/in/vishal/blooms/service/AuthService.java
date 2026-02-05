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

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // =========================
    // REGISTER USER
    // =========================
    public void registerUser(UserRequest userRequest) {

        // 1️⃣ CHECK: Agar phone number pehle se exist karta hai to error do
        User existingUser = userRepository.findByPhoneNumber(userRequest.getPhoneNumber());
        if (existingUser != null) {
            throw new RuntimeException("User already exists with this phone number!");
        }

        User user = new User();
        user.setId(String.valueOf(System.currentTimeMillis()));
        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        user.setUserName(userRequest.getUserName());
        user.setProfileUrl(userRequest.getProfileUrl());
        
        // Note: Production me password ko Encrypt karna chahiye (BCrypt)
        user.setPassword(userRequest.getPassword()); 
        
        user.setPhoneNumber(userRequest.getPhoneNumber());
        
        // 2️⃣ CHECK: Agar role null hai, to default USER set karo
        if (userRequest.getRole() == null) {
            user.setRole(Role.USER);
        } else {
            user.setRole(userRequest.getRole());
        }
        
        user.setActive(true);

        userRepository.save(user);
        System.out.println("✅ New User Registered: " + user.getPhoneNumber());
    }

    // =========================
    // LOGIN USER + JWT TOKEN
    // =========================
    public UserResponse login(LoginRequest loginRequest) {

        System.out.println("🔄 Login attempt for: " + loginRequest.getPhoneNumber());

        User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber());

        // 1️⃣ User nahi mila
        if (user == null) {
            System.out.println("❌ User not found in DB");
            throw new RuntimeException("Phone number not found");
        }

        // 2️⃣ Inactive user
        if (!user.isActive()) {
            System.out.println("❌ User is inactive");
            throw new RuntimeException("User is inactive");
        }

        // 3️⃣ Wrong password
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            System.out.println("❌ Password mismatch");
            throw new RuntimeException("Wrong password");
        }

        // 4️⃣ Handle Null Role Safety
        // Agar database me galti se role null hai to crash na ho
        String roleName = (user.getRole() != null) ? user.getRole().name() : "USER";

        // 5️⃣ Generate Token
        String token = jwtUtil.generateToken(user.getId(), roleName);
        System.out.println("✅ Token Generated Successfully");

        // 6️⃣ Prepare Full Response
        UserResponse response = new UserResponse();

        response.setUserId(user.getId());
        response.setUserName(user.getUserName());
        response.setName(user.getName());          // Added Missing Field
        response.setEmail(user.getEmail());
<<<<<<< HEAD
        response.setName(user.getName());                 // ✅ ADD
        response.setPhoneNumber(user.getPhoneNumber());   // ✅ ADD
        response.setProfileUrl(user.getProfileUrl());     // ✅ ADD
=======
        response.setPhoneNumber(user.getPhoneNumber()); // Added Missing Field
        response.setProfileUrl(user.getProfileUrl());   // Added Missing Field
>>>>>>> 8e3bb86d203d6edc6e889604ded05835a13ed44c
        response.setRole(user.getRole());
        response.setToken(token);

        return response;

    }
}
