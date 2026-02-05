package in.vishal.blooms.service;

import in.vishal.blooms.dto.LoginRequest;
import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
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

    // =========================
    // REGISTER USER
    // =========================
    public void registerUser(UserRequest userRequest) {

        User user = new User();
        user.setId(String.valueOf(System.currentTimeMillis()));
        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        user.setUserName(userRequest.getUserName());
        user.setProfileUrl(userRequest.getProfileUrl());
        user.setPassword(userRequest.getPassword());
        user.setPhoneNumber(userRequest.getPhoneNumber());
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
                user.getRole().name()
        );

        // 5️⃣ response
        UserResponse response = new UserResponse();
        response.setUserId(user.getId());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setToken(token);

        return response;
    }



}

