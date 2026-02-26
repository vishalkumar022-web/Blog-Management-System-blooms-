package in.vishal.blooms.service;

import in.vishal.blooms.dto.ConnectionResponse;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.User;
import in.vishal.blooms.models.UserConnection;
import in.vishal.blooms.repository.UserConnectionRepository;
import in.vishal.blooms.repository.UserRepository;
import in.vishal.blooms.response.ApiResponse;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserConnectionService {

    private final UserConnectionRepository connectionRepo;
    private final UserRepository userRepository;

    public UserConnectionService(UserConnectionRepository connectionRepo, UserRepository userRepository) {
        this.connectionRepo = connectionRepo;
        this.userRepository = userRepository;
    }

    // ✅ FIXED: Sabka cache udane ki jagah sirf inn dono doston ka cache udaya (Speed x100)
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#myUserId"),
            @CacheEvict(value = "users", key = "#targetUserId")
    })
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
        connection.setId(UUID.randomUUID().toString());
        connection.setFollowerId(myUserId);
        connection.setFollowingId(targetUserId);
        connection.setCreatedDTTM(LocalDateTime.now());

        connectionRepo.save(connection);

        return new ApiResponse<>(true, "Successfully followed " + targetUser.get().getUserName(), null);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#myUserId"),
            @CacheEvict(value = "users", key = "#targetUserId")
    })
    public ApiResponse<String> unfollowUser(String myUserId, String targetUserId) {
        connectionRepo.deleteByFollowerIdAndFollowingId(myUserId, targetUserId);
        return new ApiResponse<>(true, "Unfollowed successfully", null);
    }

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