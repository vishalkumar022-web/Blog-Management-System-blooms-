package in.vishal.blooms.service;

import in.vishal.blooms.dto.ConnectionResponse;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.User;
import in.vishal.blooms.models.UserConnection;
import in.vishal.blooms.repository.UserConnectionRepository;
import in.vishal.blooms.repository.UserRepository;
import in.vishal.blooms.response.ApiResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// @Service batata hai ki yahan saara 'Dimaag' (Business Logic) lagaya jayega.
@Service
public class UserConnectionService {

    // Database ke managers ko bula rahe hain
    private final UserConnectionRepository connectionRepo;
    private final UserRepository userRepository;

    // Constructor Injection (Spring Boot in managers ko khud bana kar yahan de dega)
    public UserConnectionService(UserConnectionRepository connectionRepo, UserRepository userRepository) {
        this.connectionRepo = connectionRepo;
        this.userRepository = userRepository;
    }

    // ==========================================
    // 1. FOLLOW KARNE KA LOGIC
    // ==========================================
    public ApiResponse<String> followUser(String myUserId, String targetUserId) {

        // CHECK 1: Bhai koi khud ko kaise follow kar sakta hai?
        // Agar meri ID aur samne wale ki ID same hai, toh error phek do.
        if (myUserId.equals(targetUserId)) {
            throw new ApplicationException("Bhai, aap khud ko follow nahi kar sakte!");
        }

        // CHECK 2: Jisko follow kar rahe hain, kya wo insaan exist karta hai?
        Optional<User> targetUser = userRepository.findById(targetUserId);
        if (targetUser.isEmpty() || !targetUser.get().isActive()) {
            throw new ApplicationException("User not found or account is inactive.");
        }

        // CHECK 3: Kya maine inko pehle se follow kiya hua hai?
        Optional<UserConnection> existing = connectionRepo.findByFollowerIdAndFollowingId(myUserId, targetUserId);
        if (existing.isPresent()) {
            return new ApiResponse<>(false, "Aap inko pehle se follow kar rahe hain.", null);
        }

        // AGAR SAB SAHI HAI: Toh database me entry banao
        UserConnection connection = new UserConnection();
        connection.setId(String.valueOf(System.currentTimeMillis())); // Naya Unique ID
        connection.setFollowerId(myUserId); // Main follow kar raha hu
        connection.setFollowingId(targetUserId); // Inko follow kar raha hu
        connection.setCreatedDTTM(LocalDateTime.now()); // Abhi ka time

        // Database me save kar diya! (Permanent ho gaya)
        connectionRepo.save(connection);

        return new ApiResponse<>(true, "Successfully followed " + targetUser.get().getUserName(), null);
    }

    // ==========================================
    // 2. UNFOLLOW KARNE KA LOGIC
    // ==========================================
    public ApiResponse<String> unfollowUser(String myUserId, String targetUserId) {
        // Chup-chap database manager ko bolo ki wo entry delete kar de jahan ye dono IDs sath me hain.
        connectionRepo.deleteByFollowerIdAndFollowingId(myUserId, targetUserId);
        return new ApiResponse<>(true, "Unfollowed successfully", null);
    }

    // ==========================================
    // 3. MERE FOLLOWERS LAANE KA LOGIC
    // ==========================================
    public ApiResponse<List<ConnectionResponse>> getFollowers(String userId) {
        // Pehle dekho kis kisne mujhe follow kiya hai (DB se IDs le aao)
        List<UserConnection> connections = connectionRepo.findByFollowingId(userId);

        // Ek khali dibba (List) banao jisme hum result daalenge
        List<ConnectionResponse> responseList = new ArrayList<>();

        // Har connection ke liye loop chalao
        for (UserConnection conn : connections) {
            // Us user ki poori detail UserRepository se mangwao
            userRepository.findById(conn.getFollowerId()).ifPresent(user -> {
                if (user.isActive()) {
                    // Ek naya chhota parcel (DTO) banao aur safe details daal do
                    ConnectionResponse res = new ConnectionResponse();
                    res.setUserId(user.getId());
                    res.setName(user.getName());
                    res.setUserName(user.getUserName());
                    res.setProfileUrl(user.getProfileUrl());

                    // Parcel ko dibbe me daal do
                    responseList.add(res);
                }
            });
        }
        return new ApiResponse<>(true, "Followers list fetched", responseList);
    }

    // ==========================================
    // 4. MERI FOLLOWING LIST LAANE KA LOGIC
    // ==========================================
    public ApiResponse<List<ConnectionResponse>> getFollowing(String userId) {
        // Pehle dekho main kis kisko follow kar raha hu
        List<UserConnection> connections = connectionRepo.findByFollowerId(userId);
        List<ConnectionResponse> responseList = new ArrayList<>();

        // Har ek insaan ki detail nikal kar parcel (DTO) me daalo
        for (UserConnection conn : connections) {
            userRepository.findById(conn.getFollowingId()).ifPresent(user -> {
                if (user.isActive()) {
                    ConnectionResponse res = new ConnectionResponse();
                    res.setUserId(user.getId());
                    res.setName(user.getName());
                    res.setUserName(user.getUserName());
                    res.setProfileUrl(user.getProfileUrl());
                    responseList.add(res);
                }
            });
        }
        return new ApiResponse<>(true, "Following list fetched", responseList);
    }
}