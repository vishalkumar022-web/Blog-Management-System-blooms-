package in.vishal.blooms.service;

import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.Role;
import in.vishal.blooms.models.User;
import in.vishal.blooms.repository.UserRepository;
import in.vishal.blooms.response.ApiResponse;
//import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class UserService {

    // ✅ Manual Logger Definition
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ==========================================
    // 1. GET USER BY ID (Secure)
    // ==========================================
    public ApiResponse<UserResponse> getUserById(String userId) {
        log.info("Fetching user by ID: {}", userId);

        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new ApplicationException("User not found with ID: " + userId);
            }

            User user = userOptional.get();

            // Active Check
            if (!user.isActive()) {
                throw new ApplicationException("User is inactive");
            }

            // ✅ Safe Check: NullRole handle kiya & Admin check
            if (user.getRole() != null && user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName())) {
                throw new ApplicationException("Access Denied: Cannot view Admin profile");
            }

            // Manual Mapping
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(user.getId());
            userResponse.setEmail(user.getEmail());
            userResponse.setProfileUrl(user.getProfileUrl());
            userResponse.setName(user.getName());
            userResponse.setUserName(user.getUserName());
            userResponse.setPhoneNumber(user.getPhoneNumber());
            userResponse.setRole(user.getRole());

            return new ApiResponse<>(true, "User fetched successfully", userResponse);

        } catch (ApplicationException e) {
            throw e; // Custom exception ko aage jane do
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage());
            throw new ApplicationException("Error fetching user details");
        }
    }

    // ==========================================
    // 2. GET ALL USERS (Paginated to prevent Hang)
    // ==========================================
    public ApiResponse<List<UserResponse>> getUsers(int page, int size) {
        log.info("Fetching users - Page: {}, Size: {}", page, size);

        try {
            // ✅ Pagination Setup (Sort by Name)
            // Ab DB se sirf 'size' amount ka data aayega (e.g., 10 records)
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());

            Page<User> userPage = userRepository.findAll(pageRequest);
            List<User> userList = userPage.getContent(); // List nikali

            List<UserResponse> userResponses = new ArrayList<>();

            // ✅ TUMHARA LOGIC (Loop & Filter)
            for (User user : userList) {
                // ✅ CRASH FIX: "Role.ADMIN..." ko pehle rakha aur null check lagaya
                boolean isAdmin = user.getRole() != null && user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName());

                if (user.isActive() && !isAdmin) {
                    UserResponse userResponse = new UserResponse();
                    userResponse.setUserId(user.getId());
                    userResponse.setProfileUrl(user.getProfileUrl());
                    userResponse.setName(user.getName());
                    userResponse.setRole(user.getRole());
                    userResponse.setUserName(user.getUserName());
                    userResponse.setEmail(user.getEmail());
                    userResponse.setPhoneNumber(user.getPhoneNumber());
                    // userResponse.setUserId(user.getId()); // Duplicate line removed
                    userResponses.add(userResponse);
                }
            }
            return new ApiResponse<>(true, "Users fetched successfully", userResponses);

        } catch (Exception e) {
            log.error("Error in getUsers: {}", e.getMessage());
            throw new ApplicationException("Error fetching users list");
        }
    }

    // ==========================================
    // 3. SEARCH USER (Paginated)
    // ==========================================
    public ApiResponse<List<UserResponse>> searchUsersByName(String name, int page, int size) {
        log.info("Searching users by name: {}", name);

        try {
            // ✅ Pagination Setup
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());

            // ✅ FIXED: Repository method updated to 'IsActive' with Pagination
            Page<User> userPage = userRepository.findByNameContainingIgnoreCaseAndIsActive(name, true, pageRequest);
            List<User> userList = userPage.getContent();

            List<UserResponse> userResponses = new ArrayList<>();

            // ✅ TUMHARA LOGIC
            for (User user : userList) {
                // ✅ CRASH FIX: Safe check for Admin role
                boolean isAdmin = user.getRole() != null && user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName());

                // Sirf User role walo ko dikhao, Admin ko hide karo
                if (!isAdmin) {
                    UserResponse userResponse = new UserResponse();
                    userResponse.setUserId(user.getId());
                    userResponse.setName(user.getName());
                    userResponse.setUserName(user.getUserName());
                    userResponse.setProfileUrl(user.getProfileUrl());
                    userResponse.setRole(user.getRole());
                    userResponses.add(userResponse);
                }
            }
            return new ApiResponse<>(true, "Search results fetched", userResponses);

        } catch (Exception e) {
            log.error("Error in search: {}", e.getMessage());
            throw new ApplicationException("Error occurred during search");
        }
    }

    // ==========================================
    // 4. DELETE USER (Secure)
    // ==========================================
    public ApiResponse<Boolean> deleteUser(String userId) {
        log.info("Deleting user ID: {}", userId);

        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                throw new ApplicationException("User not found for deletion");
            }
            User user = optionalUser.get();

            // ✅ Safe Check
            if (user.getRole() != null && user.getRole().equalsIgnoreCase(Role.USER.getDisplayName())) {
                user.setActive(false);
                userRepository.save(user);
                return new ApiResponse<>(true, "User deleted successfully", true);
            } else {
                throw new ApplicationException("Only users with role 'USER' can be deleted");
            }
        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Delete failed: {}", e.getMessage());
            throw new ApplicationException("Error deleting user");
        }
    }

    // ==========================================
    // 5. UPDATE USER (Duplicate Check + Validation)
    // ==========================================
    public ApiResponse<UserResponse> updateUser(UserRequest userRequest) {
        log.info("Updating user ID: {}", userRequest.getUserId());

        if (userRequest.getUserId() == null) {
            throw new ApplicationException("User ID is not required for update");
        }

        try {
            Optional<User> optionalUser = userRepository.findById(userRequest.getUserId());
            if (optionalUser.isEmpty()) {
                throw new ApplicationException("User not found");
            }
            User user = optionalUser.get();

            // ✅ Safe Check
            boolean isAdmin = user.getRole() != null && user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName());

            if (isAdmin) {
                throw new ApplicationException("Cannot update Admin user");
            }

            // =========================================================
            // ✅ CRITICAL: DUPLICATE EMAIL & PHONE CHECK (Code Fatne se bachayega)
            // =========================================================

            // 1. Check Email Duplicacy (Agar naya email alag hai purane se)
            if (userRequest.getEmail() != null && !userRequest.getEmail().equals(user.getEmail())) {
                if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
                    throw new ApplicationException("Email '" + userRequest.getEmail() + "' pehle se registered hai.");
                }
                user.setEmail(userRequest.getEmail());
            }

            // 2. Check Phone Duplicacy
            if (userRequest.getPhoneNumber() != null && !userRequest.getPhoneNumber().equals(user.getPhoneNumber())) {
                if (userRepository.findByPhoneNumber(userRequest.getPhoneNumber()).isPresent()) {
                    throw new ApplicationException("Phone '" + userRequest.getPhoneNumber() + "' pehle se registered hai.");
                }
                user.setPhoneNumber(userRequest.getPhoneNumber());
            }

            // Update Remaining Fields
            if (userRequest.getName() != null) user.setName(userRequest.getName());
            if (userRequest.getPassword() != null) user.setPassword(userRequest.getPassword());
            if (userRequest.getUserName() != null) user.setUserName(userRequest.getUserName());
            if (userRequest.getProfileUrl() != null) user.setProfileUrl(userRequest.getProfileUrl());
            if (userRequest.getRole() != null) user.setRole(userRequest.getRole());

            userRepository.save(user);

            // Response Mapping
            UserResponse userResponse = new UserResponse();
            userResponse.setUserName(user.getUserName());
            userResponse.setUserId(user.getId());
            userResponse.setName(user.getName());
            userResponse.setEmail(user.getEmail());
            userResponse.setProfileUrl(user.getProfileUrl());
            userResponse.setRole(user.getRole());

            return new ApiResponse<>(true, "User updated successfully", userResponse);

        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Update failed: {}", e.getMessage());
            // Agar DB save me koi aur error aaye (race condition)
            throw new ApplicationException("Database error during update: " + e.getMessage());
        }
    }
}