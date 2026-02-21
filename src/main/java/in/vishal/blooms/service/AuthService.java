// Ye batata hai ki ye file hamare project ke kis folder (folder ko package bolte hain) me rakhi hai.
// Agar ye nahi likhenge, toh Java ko ye file milegi hi nahi aur error de dega.
package in.vishal.blooms.service;

// ==========================================
// IMPORTS: Bahar se saman (tools) mangwana
// ==========================================
// Jaise ghar banane ke liye bahar se eent aur cement mangwana padta hai,
// waise hi code likhne ke liye pehle se bani hui files (classes) import karni padti hain.
// Agar ye imports nahi honge, toh niche likhe gaye words (jaise User, Map, RestTemplate) pe laal (red) error aayega.
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

// @Service Spring Boot ko batata hai ki "Bhai, ye file dimaag (logic) lagane wali file hai."
// Agar ye nahi lagayenge, toh Spring Boot isko start nahi karega aur app fail ho jayega.
@Service
public class AuthService {

    // Logger ka kaam hai terminal/console par messages print karna (jaise console.log hota hai JS me).
    // Agar ye nahi hota, toh humein pata hi nahi chalta ki code kahan tak chala aur kahan fail hua.
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    // Ye database se baat karne wala "Manager" hai.
    private final UserRepository userRepository;

    // Ye Security guard hai jo token (entry pass) banata hai.
    private final JwtUtil jwtUtil;

    // ==========================================
    // NAYA TOOL: RestTemplate (Hamara Smart Delivery Boy) 🚚
    // ==========================================
    // EXPLANATION: Ye Spring Boot ka tool hai jo internet par kisi dusri website (yahan Brevo) ko message (API request) bhejta hai.
    // KYU HAI: Kyunki ab hum SMTP (Post office) use nahi kar rahe. Hum direct internet highway (Port 443) use karenge.
    // NAHI HOTA TOH: Hamara backend kisi bhi dusre server (Brevo/AI) se baat nahi kar pata.
    private final RestTemplate restTemplate = new RestTemplate();

    // @Value ka kaam hai 'application.properties' file me se secret chura kar yahan is variable me daalna.
    // NAHI HOTA TOH: Humein password directly code me likhna padta, jo ki bohot risky hai.
    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // Constructor: Jab ye class banti hai, toh Spring Boot automatically Inko (Repository, JwtUtil) isme daal deta hai.
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // Role check karne ka helper function (Sirf 'user' aur 'admin' allow karega)
    private boolean isValidRole(String role) {
        if (role == null) return false;
        for (Role r : Role.values()) {
            if (r.getDisplayName().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    // Naya user banane (Register) ka logic
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

            userRepository.save(user); // Data save ho gaya
            return new ApiResponse<>(true, "User registered successfully.", null);
        } catch (Exception e) {
            throw new ApplicationException("Error saving user: " + e.getMessage());
        }
    }

    // Login karne ka logic
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
        response.setToken(token); // User ko token de diya

        return new ApiResponse<>(true, "Login Successful", response);
    }

    // ================= FORGOT PASSWORD LOGIC =================
    // Ye 'Map' ek temporary diary hai. Isme hum save karenge "Kis email pe kya OTP bheja hai".
    // NAHI HOTA TOH: Hum bhool jate ki user ko kya OTP bheja tha, aur verify nahi kar paate.
    private Map<String, String> otpStorage = new HashMap<>();

    // Email bhejne wala main function
    public ApiResponse<String> sendOtp(String email) {

        // 1. Pehle check kiya ki ye email humare database me hai bhi ya nahi.
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new ApplicationException("Email ID not exist buddy");
        }

        // 2. 4-digit ka random OTP banaya.
        String otp = String.valueOf(new Random().nextInt(9000) + 1000);

        try {
            // 3. Brevo API ka Pata (Address)
            // Ye wo website link hai jahan humara RestTemplate request bhejega.
            String url = "https://api.brevo.com/v3/smtp/email";

            // 4. Headers (Lifafa ke bahar ki jankari)
            // Jaise postman ko ID card dikhana padta hai, yahan hum HttpHeaders me apni API Key de rahe hain.
            // NAHI HOTA TOH: Brevo humein pehchan nahi pata aur bolta "Bhai tu kaun hai? Main email nahi bhejunga!"
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // Bata rahe hain ki hum JSON (khabar) bhej rahe hain
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("api-key", brevoApiKey); // Humari secret chabhi

            // 5. Body (Lifafe ke andar ki chitthi)
            // Hum ek Map (dictionary) bana rahe hain jisme bata rahe hain kisko bhejna hai, kya bhejna hai.
            // NAHI HOTA TOH: Khali email chala jata ya error aa jata.
            Map<String, Object> body = new HashMap<>();

            // Kon bhej raha hai? (Sender)
            body.put("sender", Map.of("name", "Blooms App", "email", senderEmail));

            // Kisko bhejna hai? (Receiver - List isliye kyunki 1 se zyada log ko bhi bhej sakte hain)
            body.put("to", List.of(Map.of("email", email)));

            // Email ka title (Subject)
            body.put("subject", "Blooms Password Reset OTP");

            // Asli message
            body.put("textContent", "Hello,\n\nYour OTP for password reset is: " + otp + "\n\nThanks,\nBlooms Team");

            // 6. Packet taiyar karna (HttpEntity)
            // Header aur Body dono ko mila kar ek packet bana diya.
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 7. ASLI MAGIC: Internet par packet bhejna! 🚀
            // RestTemplate packet uthata hai aur us URL (Brevo) par POST kar deta hai.
            // Ye Port 443 (Highway) se jata hai, isliye Render ka security guard isko nahi rokega!
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            // 8. Result Check Karna
            // Agar code '200' se '299' ke beech me aaya (matlab Success)
            if (response.getStatusCode().is2xxSuccessful()) {
                otpStorage.put(email, otp); // Diary me likh liya
                log.info("OTP sent to: {} using Brevo API", email);
                return new ApiResponse<>(true, "OTP sent successfully to " + email, null);
            } else {
                // Agar fail hua toh error feko
                throw new ApplicationException("Failed to send email via Brevo.");
            }

        } catch (Exception e) {
            // Agar internet band hai ya API key galat hai toh program crash hone se bachane ke liye catch block.
            log.error("Brevo API Error: {}", e.getMessage());
            throw new ApplicationException("Failed to send email. Check internet connection or API Key.");
        }
    }

    // OTP aane ke baad, password change karne ka logic
    public ApiResponse<String> resetPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        String userOtp = request.getOtp();
        String newPassword = request.getNewPassword();

        // Check 1: User ne OTP dala hai na?
        if (userOtp == null || userOtp.isEmpty()) throw new ApplicationException("Please give otp first");

        // Check 2: Kya is email ko OTP bheja tha humne?
        if (!otpStorage.containsKey(email)) throw new ApplicationException("Please send OTP first!");

        // Check 3: Jo OTP user laya hai, kya wo humari diary se match kar raha hai?
        if (!otpStorage.get(email).equals(userOtp)) throw new ApplicationException("Invalid OTP! Try again.");

        // Agar sab sahi hai toh naya password save kar do
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApplicationException("User not found"));
        user.setPassword(newPassword);
        userRepository.save(user);

        // Diary se OTP hata do, taaki dubara koi wahi OTP use na kare.
        otpStorage.remove(email);

        return new ApiResponse<>(true, "Password changed successfully", null);
    }
}