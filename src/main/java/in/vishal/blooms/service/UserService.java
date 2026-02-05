package in.vishal.blooms.service;

import in.vishal.blooms.dto.LoginRequest;
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



    // Search user by its id :-

    public UserResponse getUserById(String userId) {

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();

        if (!user.isActive()) {
            return null;
        }

        if(user.getRole() != Role.ADMIN){
            return null;
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setProfileUrl(user.getProfileUrl());
        userResponse.setName(user.getName());
        userResponse.setUserName(user.getUserName());
        //  password intentionally NOT set
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setRole(user.getRole());
        return userResponse;
    }



    // GetAll user

    public List<UserResponse> getUsers(){

        List<User>userList = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for(User user : userList){
            if(user.isActive() && user.getRole() != Role.ADMIN){
                UserResponse userResponse = new UserResponse();

                userResponse.setUserId(user.getId());
                userResponse.setEmail(user.getEmail());
                userResponse.setProfileUrl(user.getProfileUrl());
                userResponse.setName(user.getName());

                userResponse.setUserName(user.getUserName());
                userResponse.setPhoneNumber(user.getPhoneNumber());
                userResponses.add(userResponse);

            }
        }
        return userResponses;
    }


    //Delete user

    public boolean deleteUser(String UserId){

        Optional<User> optionalUser = userRepository.findById(UserId);

        if(optionalUser.isEmpty()) {
            return false ;
        }


        User user = optionalUser.get();

        if(user.getRole()==Role.USER){
            user.setActive(false);
        userRepository.save(user);
        return true ;
    }
        return false;

    }

    // Update User

    public UserResponse updateUser(UserRequest userRequest ){

        UserResponse userResponse = new UserResponse();
        if(userRequest.getUserId()==null){
            System.out.println("this user id is not exist so, you cannot update this user data ");
            return userResponse;
        }
        Optional<User> optionalUser = userRepository.findById(userRequest.getUserId());

        User user = optionalUser.get();

if(user.getRole()!=Role.ADMIN) {
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

        return userResponse;
    }
}






