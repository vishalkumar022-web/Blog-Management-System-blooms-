package in.vishal.blooms.service;

import in.vishal.blooms.dto.LoginRequest;
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

    // Create user

    public void createUser(UserRequest userRequest){
        User user = new User();

        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        user.setUserName(userRequest.getUserName());
        user.setProfileUrl(userRequest.getProfileUrl());
        user.setPassword(userRequest.getPassword());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setActive(true);
        user.setId(String.valueOf(System.currentTimeMillis()));
        userRepository.save(user);
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

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setProfileUrl(user.getProfileUrl());
        userResponse.setName(user.getName());
        userResponse.setUserName(user.getUserName());
        //  password intentionally NOT set
        userResponse.setPhoneNumber(user.getPhoneNumber());
        return userResponse;
    }



    // GetAll user

    public List<UserResponse> getUsers(){

        List<User>userList = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for(User user : userList){
            if(user.isActive()){
                UserResponse userResponse = new UserResponse();

                userResponse.setUserId(user.getId());
                userResponse.setEmail(user.getEmail());
                userResponse.setProfileUrl(user.getProfileUrl());
                userResponse.setName(user.getName());
                userResponse.setPassword(user.getPassword());
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

        user.setActive(false);
        userRepository.save(user);
        return true ;
    }



    public String loginUser(LoginRequest loginRequest) {

        User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber());

        // user hi nahi mila
        if (user == null) {
            System.out.println("Login Failed ❌ : Phone number not found");
            return "Login Failed";
        }

        // inactive user
        if (!user.isActive()) {
            System.out.println("Login Failed ❌ : User inactive");
            return "Login Failed";
        }

        // password match
        if (user.getPassword().equals(loginRequest.getPassword())) {
            System.out.println("Login Successful ✅");
            return "Login Successful";
        }

        System.out.println("Login Failed ❌ : Wrong password");
        return "Login Failed";
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


        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setUserName(userRequest.getUserName());
        user.setProfileUrl(userRequest.getProfileUrl());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        userRepository.save(user);

        userResponse.setUserName(user.getUserName());
        userResponse.setUserId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());
        userResponse.setProfileUrl(user.getProfileUrl());

        return userResponse;
    }
}






