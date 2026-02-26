package in.vishal.blooms.service;

import in.vishal.blooms.dto.*;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.*;
import in.vishal.blooms.repository.*;
import in.vishal.blooms.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final BlogRepository blogRepository;
    private final UserConnectionRepository userConnectionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository, BlogRepository blogRepository , UserConnectionRepository userConnectionRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.blogRepository = blogRepository;
        this.userConnectionRepository = userConnectionRepository;
    }

    @Cacheable(value = "users", key = "#userId")
    public ApiResponse<UserResponse> getUserById(String userId) {
        log.info("Fetching user by ID: {}", userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApplicationException("User not found with ID: " + userId));

            if (!user.isActive()) throw new ApplicationException("User is inactive");

            if (user.getRole() != null && user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName())) {
                throw new ApplicationException("Access Denied: Cannot view Admin profile");
            }

            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(user.getId());
            userResponse.setEmail(user.getEmail());
            userResponse.setProfileUrl(user.getProfileUrl());
            userResponse.setName(user.getName());
            userResponse.setUserName(user.getUserName());
            userResponse.setPhoneNumber(user.getPhoneNumber());
            userResponse.setRole(user.getRole());

            userResponse.setMyCreatedCategories(categoryRepository.findByCreatedBy(userId).stream().filter(Category::isActive).map(Category::getName).toList());
            userResponse.setMyCreatedSubCategories(subCategoryRepository.findByCreatedBy(userId).stream().filter(SubCategory::getActive).map(SubCategory::getName).toList());
            userResponse.setMyCreatedBlogs(blogRepository.findByAuthorId(userId).stream().filter(Blog::getActive).map(Blog::getTitle).toList());

            userResponse.setFollowerCount(userConnectionRepository.countByFollowingId(userId));
            userResponse.setFollowingCount(userConnectionRepository.countByFollowerId(userId));

            return new ApiResponse<>(true, "User fetched successfully", userResponse);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new ApplicationException("Error fetching user details");
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public ApiResponse<UserResponse> updateUser(UserRequest userRequest) {
        User user = userRepository.findById(userRequest.getUserId())
                .orElseThrow(() -> new ApplicationException("User not found"));

        if (user.getRole() != null && user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName())) {
            throw new ApplicationException("Cannot update Admin user");
        }

        if (userRequest.getEmail() != null && !userRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) throw new ApplicationException("Email already exists.");
            user.setEmail(userRequest.getEmail());
        }

        if (userRequest.getPhoneNumber() != null && !userRequest.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.findByPhoneNumber(userRequest.getPhoneNumber()).isPresent()) throw new ApplicationException("Phone already exists.");
            user.setPhoneNumber(userRequest.getPhoneNumber());
        }

        if (userRequest.getName() != null) user.setName(userRequest.getName());
        if (userRequest.getPassword() != null) user.setPassword(passwordEncoder.encode(userRequest.getPassword())); // ✅ Secure update
        if (userRequest.getUserName() != null) user.setUserName(userRequest.getUserName());
        if (userRequest.getProfileUrl() != null) user.setProfileUrl(userRequest.getProfileUrl());

        userRepository.save(user);

        UserResponse res = new UserResponse();
        res.setUserId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setRole(user.getRole());
        return new ApiResponse<>(true, "User updated successfully", res);
    }

    @CacheEvict(value = "users", allEntries = true)
    public ApiResponse<Boolean> deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApplicationException("User not found"));
        if (user.getRole() != null && user.getRole().equalsIgnoreCase(Role.USER.getDisplayName())) {
            user.setActive(false);
            userRepository.save(user);
            return new ApiResponse<>(true, "User deleted successfully", true);
        }
        throw new ApplicationException("Only normal users can be deleted");
    }

    public ApiResponse<List<UserResponse>> searchUsersByName(String name, int page, int size) {
        Page<User> userPage = userRepository.findByNameContainingIgnoreCaseAndIsActive(name, true, PageRequest.of(page, size));
        List<UserResponse> list = userPage.getContent().stream()
                .filter(u -> !Role.ADMIN.getDisplayName().equalsIgnoreCase(u.getRole()))
                .map(u -> {
                    UserResponse r = new UserResponse();
                    r.setUserId(u.getId());
                    r.setName(u.getName());
                    r.setUserName(u.getUserName());
                    r.setProfileUrl(u.getProfileUrl());
                    return r;
                }).toList();
        return new ApiResponse<>(true, "Search results fetched", list);
    }
}