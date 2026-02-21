package in.vishal.blooms.service;

import in.vishal.blooms.dto.ConnectionResponse;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.User;
import in.vishal.blooms.models.UserConnection;
import in.vishal.blooms.repository.UserConnectionRepository;
import in.vishal.blooms.repository.UserRepository;
import in.vishal.blooms.response.ApiResponse;

// ==========================================
// NAYA TOOL: CacheEvict (Notice Board saaf karne wala)
// ==========================================
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserConnectionService {

    private final UserConnectionRepository connectionRepo;
    private final UserRepository userRepository;

    public UserConnectionService(UserConnectionRepository connectionRepo, UserRepository userRepository) {
        this.connectionRepo = connectionRepo;
        this.userRepository = userRepository;
    }

    // ==========================================
    // 1. FOLLOW KARNE KA LOGIC
    // ==========================================
    // @CacheEvict ka jaadu: Jaise hi koi follow button dabayega,
    // ye line turant 'users' naam ke Notice Board (Redis cache) ko mita degi.
    // Taki jab '/me' API dobara chale toh wo naya count database se gin kar laye!
    @CacheEvict(value = "users", allEntries = true)
    public ApiResponse<String> followUser(String myUserId, String targetUserId) {

        if (myUserId.equals(targetUserId)) {
            throw new ApplicationException("Bhai, aap khud ko follow nahi kar sakte!");
        }

        Optional<User> targetUser = userRepository.findById(targetUserId);
        if (targetUser.isEmpty() || !targetUser.get().isActive()) {
            throw new ApplicationException("User not found or account is inactive.");
        }

        Optional<UserConnection> existing = connectionRepo.findByFollowerIdAndFollowingId(myUserId, targetUserId);
        if (existing.isPresent()) {
            return new ApiResponse<>(false, "Aap inko pehle se follow kar rahe hain.", null);
        }

        UserConnection connection = new UserConnection();
        connection.setId(String.valueOf(System.currentTimeMillis()));
        connection.setFollowerId(myUserId);
        connection.setFollowingId(targetUserId);
        connection.setCreatedDTTM(LocalDateTime.now());

        connectionRepo.save(connection);

        return new ApiResponse<>(true, "Successfully followed " + targetUser.get().getUserName(), null);
    }

    // ==========================================
    // 2. UNFOLLOW KARNE KA LOGIC
    // ==========================================
    // Unfollow karne pe doston ki ginti kam hogi, isliye yahan bhi board (cache) saaf karna zaroori hai.
    @CacheEvict(value = "users", allEntries = true)
    public ApiResponse<String> unfollowUser(String myUserId, String targetUserId) {
        connectionRepo.deleteByFollowerIdAndFollowingId(myUserId, targetUserId);
        return new ApiResponse<>(true, "Unfollowed successfully", null);
    }

    // ==========================================
    // 3. MERE FOLLOWERS LAANE KA LOGIC
    // ==========================================
    public ApiResponse<List<ConnectionResponse>> getFollowers(String userId) {
        List<UserConnection> connections = connectionRepo.findByFollowingId(userId);
        List<ConnectionResponse> responseList = new ArrayList<>();

        for (UserConnection conn : connections) {
            userRepository.findById(conn.getFollowerId()).ifPresent(user -> {
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
        return new ApiResponse<>(true, "Followers list fetched", responseList);
    }

    // ==========================================
    // 4. MERI FOLLOWING LIST LAANE KA LOGIC
    // ==========================================
    public ApiResponse<List<ConnectionResponse>> getFollowing(String userId) {
        List<UserConnection> connections = connectionRepo.findByFollowerId(userId);
        List<ConnectionResponse> responseList = new ArrayList<>();

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