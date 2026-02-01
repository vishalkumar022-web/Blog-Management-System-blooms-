package in.vishal.blooms.controller;

import in.vishal.blooms.dto.AdminLoginRequest;
import in.vishal.blooms.dto.AdminRequest;
import in.vishal.blooms.dto.AdminResponse;
import in.vishal.blooms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/signup")
    public void createAdmin(@RequestBody AdminRequest adminRequest) {
        adminService.createAdmin(adminRequest);
    }

    @GetMapping
    public AdminResponse getAdminById(@RequestParam String AdminId) {
        return adminService.getAdminById(AdminId);
    }

    @GetMapping("/all")
    public List<AdminResponse> getAdmins() {
        return adminService.getAdmins();
    }

    @PostMapping("/login")
    public String loginAdmin(@RequestBody AdminLoginRequest loginRequest) {
        return adminService.loginAdmin(loginRequest);
    }

    @DeleteMapping
    public boolean deleteAdmin(@RequestParam String AdminId) {
        return adminService.deleteAdmin(AdminId);
    }

    @PutMapping
    public AdminResponse updateAdmin(@RequestBody AdminRequest adminRequest) {
        return adminService.updateAdmin(adminRequest);
    }
}
