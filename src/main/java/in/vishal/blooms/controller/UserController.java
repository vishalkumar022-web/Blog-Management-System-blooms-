package in.vishal.blooms.controller;

import in.vishal.blooms.dto.LoginRequest;
import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/User")
public class UserController {
    @Autowired
    private  UserService userService;

    // Create user
    @PostMapping("/api/account/signup")
    public void CreateUser(@RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
    }

    // Search user by its id :-
    @GetMapping
    public UserResponse getUserById(@RequestParam String UserId) {

        return userService.getUserById(UserId);


    }


    @GetMapping("/all")
    public List<UserResponse> getUsers() {

        return userService.getUsers();
    }



    @PostMapping("/api/account/login")
    public String loginUser(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }



    @DeleteMapping
    public boolean deleteUser(@RequestParam String UserId) {

        return userService.deleteUser(UserId);
    }


    @PutMapping
    public UserResponse updateUser(@RequestBody UserRequest userRequest) {

        return userService.updateUser(userRequest);
    }

}
