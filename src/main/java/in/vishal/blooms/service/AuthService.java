package in.vishal.blooms.service;

import in.vishal.blooms.dto.ForgotPasswordRequest;
import in.vishal.blooms.dto.LoginRequest;
import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.Role;
import in.vishal.blooms.models.User;
import in.vishal.blooms.repository.UserRepository;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final JavaMailSender javaMailSender;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.javaMailSender = javaMailSender;
    }

    // ================= HELPER METHOD =================
    private boolean isValidRole(String role) {
        if (role == null) return false;
        for (Role r : Role.values()) {
            if (r.getDisplayName().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    // ================= REGISTER USER =================
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
            return new ApiResponse<>(true, "User registered successfully.", null);
        } catch (Exception e) {
            throw new ApplicationException("Error saving user: " + e.getMessage());
        }
    }

    // ================= LOGIN USER =================
    public ApiResponse<UserResponse> login(LoginRequest loginRequest) {
        User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
                .orElseThrow(() -> new ApplicationException("Phone number not found"));

        if (!user.isActive()) throw new ApplicationException("User is inactive");
        if (!user.getPassword().equals(loginRequest.getPassword())) throw new ApplicationException("Wrong password");

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

    // ================= FORGOT PASSWORD LOGIC =================
    // [EXPLANATION]: Yahan humne ek 'Map' banaya hai. Ye ek temporary diary jaisa hai.
    // Isme hum store karenge: "Kis Email ko -> Kya OTP bheja".
    // Agar ye nahi banate, toh OTP bhej toh dete, par jab user wapas OTP lekar aata,
    // toh hume pata hi nahi chalta ki asli OTP kya tha verify karne ke liye.
    private Map<String, String> otpStorage = new HashMap<>();

    public ApiResponse<String> sendOtp(String email) {
        // 1. Email DB Check
        // [EXPLANATION]: Sabse pehle check karo ki ye email humare database mein hai bhi ya nahi.
        // Agar DB mein nahi hai, toh OTP mat bhejo. Ye security ke liye hai taaki koi faltu email pe spam na kare.
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new ApplicationException("Email ID not exist buddy");
        }

        // 2. Generate OTP
        // [EXPLANATION]: Yahan hum 4 digit ka random number bana rahe hain.
        // 'nextInt(9000)' deta hai 0 se 8999 tak number.
        // '+ 1000' karne se wo ban jata hai 1000 se 9999.
        // Agar '+ 1000' nahi karte, toh kabhi kabhi '50' ya '9' aa jata jo 4 digit nahi hota.
        String otp = String.valueOf(new Random().nextInt(9000) + 1000);

        // 3. Send Email
        // [EXPLANATION]: 'try' block yahan isliye lagaya kyuki email bhejna ek "Risk" wala kaam hai.
        // Internet band ho sakta hai, password galat ho sakta hai, Google ka server down ho sakta hai.
        try {
            // [EXPLANATION]: Ye ek khali lifafa (envelope) bana rahe hain email likhne ke liye.
            SimpleMailMessage message = new SimpleMailMessage();
            // [EXPLANATION]: Lifafe par pata (address) likha ki kisko bhejna hai.
            message.setTo(email);
            // [EXPLANATION]: Email ka subject (Title) set kiya.
            message.setSubject("Password Reset OTP");
            // [EXPLANATION]: Asli content (OTP) lifafe ke andar dala.
            message.setText("Hello, Your OTP is: " + otp);

            // [EXPLANATION]: Ye wo line hai jahan "Send" button dabta hai.
            // Spring Boot ab Google ke server se baat karke email bhejega.
            javaMailSender.send(message); // Yahan Action Hoga

            // [EXPLANATION]: Email chala gaya! Ab turant apni diary (Map) mein note kar lo.
            // Ki "Is email" ke liye "Ye OTP" bheja hai. Taaki baad mein verify kar sakein.
            otpStorage.put(email, otp);
            log.info("OTP sent to: {}", email);
            return new ApiResponse<>(true, "OTP sent successfully to " + email, null);

        } catch (MailAuthenticationException e) {
            // Ab ye wala pakdega tumhara error
            // [EXPLANATION]: CATCH 1: Ye tab aayega jab tumhara application.properties mein EMAIL ya APP PASSWORD galat hoga.
            // Google bolega "Bhai password match nahi kar raha, main email nahi bhejunga".
            log.error("Auth Failed: {}", e.getMessage());
            throw new ApplicationException("Server Config Error: Password galat hai ya App Password expire ho gaya hai.");
        } catch (MailSendException e) {
            // [EXPLANATION]: CATCH 2: Ye tab aayega jab "To" wala email galat format ka ho (jaise bina @gmail.com).
            // Ya fir wo email exist hi na karta ho Google ke paas.
            log.error("Invalid Email: {}", e.getMessage());
            throw new ApplicationException("Email address galat hai ya exist nahi karta.");
        } catch (Exception e) {
            // [EXPLANATION]: CATCH 3: Ye "Baap Catch" hai. Jo upar ke 2 mein nahi pakda gaya, wo yahan aayega.
            // Jaise: Internet connection toot gaya, ya koi aur unknown error.
            log.error("Unknown Error: {}", e.getMessage());
            throw new ApplicationException("Failed to send email. Check internet connection.");
        }
    }

    public ApiResponse<String> resetPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        String userOtp = request.getOtp();
        String newPassword = request.getNewPassword();

        // [EXPLANATION]: Pehle check karo user ne OTP ka dabba khali toh nahi chhoda?
        if (userOtp == null || userOtp.isEmpty()) throw new ApplicationException("Please give otp first");

        // [EXPLANATION]: Check karo ki kya humne is email pe kabhi OTP bheja bhi tha?
        // Agar Map (diary) mein ye email nahi hai, matlab user ne pehla step skip kiya hai.
        if (!otpStorage.containsKey(email)) throw new ApplicationException("Please send OTP first!");

        // [EXPLANATION]: Sabse main check: User ka diya hua OTP == Diary wala OTP?
        // Agar match nahi kiya, toh error feko.
        if (!otpStorage.get(email).equals(userOtp)) throw new ApplicationException("Invalid OTP! Try again.");

        // [EXPLANATION]: Ab sab sahi hai. Database se user nikalo.
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApplicationException("User not found"));
        // [EXPLANATION]: User ka password naye wale password se badal do.
        user.setPassword(newPassword);
        // [EXPLANATION]: Database mein wapas save kar do (Update query chalegi).
        userRepository.save(user);

        // [EXPLANATION]: Kaam khatam! Ab Map (diary) se wo OTP delete kar do.
        // Kyuki OTP ek hi baar use hona chahiye. Agar delete nahi kiya toh user purana OTP wapas daal ke hack kar sakta hai.
        otpStorage.remove(email);

        return new ApiResponse<>(true, "Password changed successfully", null);
    }
}