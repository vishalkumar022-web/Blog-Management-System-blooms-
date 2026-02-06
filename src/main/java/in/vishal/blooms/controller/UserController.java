package in.vishal.blooms.controller;

import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.security.JwtUtil;
import in.vishal.blooms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/User")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // 1. Get User By ID (Public)
    @GetMapping
    public UserResponse getUserById(@RequestParam String UserId) {
        return userService.getUserById(UserId);
    }

    // 2. Get All Users (Public)
    @GetMapping("/all")
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    // 3. Search User By Name (Public)
    @GetMapping("/search")
    public List<UserResponse> searchUsers(@RequestParam String name) {
        return userService.searchUsersByName(name);
    }

    // 4. Update User (SECURE: Token se ID lega)
    @PutMapping
    public UserResponse updateUser(@RequestBody UserRequest userRequest,
                                   @RequestHeader("Authorization") String tokenHeader) {

        // Token se ID nikalo
        String token = tokenHeader.substring(7);
        String userIdFromToken = jwtUtil.extractUserId(token);

        // Request me ID set karo taki wo kisi aur ka update na kar sake
        userRequest.setUserId(userIdFromToken);

        return userService.updateUser(userRequest);
    }

    // 5. Delete User (SECURE: Token se ID lega)
    @DeleteMapping
    public boolean deleteUser(@RequestHeader("Authorization") String tokenHeader) {

        // Token se ID nikalo
        String token = tokenHeader.substring(7);
        String userIdFromToken = jwtUtil.extractUserId(token);

        // Apni hi ID delete karega
        return userService.deleteUser(userIdFromToken);
    }
}