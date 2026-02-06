package in.vishal.blooms.service;

import in.vishal.blooms.models.Role;
import in.vishal.blooms.repository.UserRepository;
import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository ;
    }

    // Search user by its id
    public UserResponse getUserById(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) return null;

        User user = userOptional.get();
        if (!user.isActive()) return null;

        // ✅ Safe Check: NullRole handle kiya
        if(user.getRole() != null && user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName())) {
            return null;
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setProfileUrl(user.getProfileUrl());
        userResponse.setName(user.getName());
        userResponse.setUserName(user.getUserName());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setRole(user.getRole());
        return userResponse;
    }

    // GetAll user
    public List<UserResponse> getUsers(){
        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();

        for(User user : userList){
            // ✅ CRASH FIX: "Role.ADMIN..." ko pehle rakha aur null check lagaya
            boolean isAdmin = user.getRole() != null && user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName());

            if(user.isActive() && !isAdmin){
                UserResponse userResponse = new UserResponse();
                userResponse.setUserId(user.getId());
                userResponse.setProfileUrl(user.getProfileUrl());
                userResponse.setName(user.getName());
                userResponse.setRole(user.getRole());
                userResponse.setUserName(user.getUserName());
                userResponse.setUserId(user.getId());
                userResponses.add(userResponse);
            }
        }
        return userResponses;
    }

    // ✅ NEW: Search User Publicly
    public List<UserResponse> searchUsersByName(String name) {

        // ✅ FIXED: Repository method updated to 'IsActive'
        List<User> userList = userRepository.findByNameContainingIgnoreCaseAndIsActive(name, true);
        List<UserResponse> userResponses = new ArrayList<>();

        for(User user : userList){

            // ✅ CRASH FIX: Safe check for Admin role
            boolean isAdmin = user.getRole() != null && user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName());

            // Sirf User role walo ko dikhao, Admin ko hide karo
            if(!isAdmin){
                UserResponse userResponse = new UserResponse();
                userResponse.setUserId(user.getId());
                userResponse.setName(user.getName());
                userResponse.setUserName(user.getUserName());
                userResponse.setProfileUrl(user.getProfileUrl());
                userResponse.setRole(user.getRole());

                userResponses.add(userResponse);
            }
        }
        return userResponses;
    }

    // Delete user
    public boolean deleteUser(String UserId){
        Optional<User> optionalUser = userRepository.findById(UserId);
        if(optionalUser.isEmpty()) return false ;
        User user = optionalUser.get();

        // ✅ Safe Check
        if(user.getRole() != null && user.getRole().equalsIgnoreCase(Role.USER.getDisplayName())){
            user.setActive(false);
            userRepository.save(user);
            return true ;
        }
        return false;
    }

    // Update User
    public UserResponse updateUser(UserRequest userRequest ){
        UserResponse userResponse = new UserResponse();
        if(userRequest.getUserId()==null) return userResponse;

        Optional<User> optionalUser = userRepository.findById(userRequest.getUserId());
        User user = optionalUser.get();

        // ✅ Safe Check
        boolean isAdmin = user.getRole() != null && user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName());

        if(!isAdmin) {
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());
            user.setUserName(userRequest.getUserName());
            user.setProfileUrl(userRequest.getProfileUrl());
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setRole(userRequest.getRole());
            userRepository.save(user);
        }
        userResponse.setUserName(user.getUserName());
        userResponse.setUserId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setProfileUrl(user.getProfileUrl());
        userResponse.setRole(user.getRole());
        return userResponse;
    }
}