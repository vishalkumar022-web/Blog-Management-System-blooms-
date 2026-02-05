package in.vishal.blooms.controller;

import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.models.*;
import in.vishal.blooms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin") // ✅ lowercase & standard
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ================= ADMINS =================

    @GetMapping("/details") // ✅ Fixed URL
    public UserResponse getAdminById(@RequestParam String userId) {
        return adminService.getAdminById(userId);
    }

    @GetMapping("/list") // ✅ Fixed URL
    public List<UserResponse> getAdmins() {
        return adminService.getAdmins();
    }

    @PutMapping("/update") // ✅ Fixed URL
    public UserResponse updateAdmin(@RequestBody UserRequest adminRequest) {
        return adminService.updateAdmin(adminRequest);
    }

    // ================= USERS =================

    @GetMapping("/users/all")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/users/details") // ✅ Fixed
    public User getUserById(@RequestParam String userId) {
        return adminService.getUserById(userId);
    }

    @DeleteMapping("/users/delete") // ✅ Fixed
    public boolean deleteUser(@RequestParam String userId) {
        return adminService.deleteUserById(userId);
    }

    // ================= CATEGORY =================

    @GetMapping("/categories/all")
    public List<Category> getAllCategories() {
        return adminService.getAllCategories();
    }

    @GetMapping("/categories/details")
    public Category getCategoryById(@RequestParam String categoryId) {
        return adminService.getCategoryById(categoryId);
    }

    @DeleteMapping("/categories/delete")
    public boolean deleteCategory(@RequestParam String categoryId) {
        return adminService.deleteCategoryById(categoryId);
    }

    @PutMapping("/categories/status")
    public boolean updateCategoryStatus(@RequestParam String categoryId, @RequestParam Status status) {
        return adminService.updateCategoryStatus(categoryId, status);
    }

    // ================= SUB CATEGORY =================

    @GetMapping("/subcategories/all")
    public List<SubCategory> getAllSubCategories() {
        return adminService.getAllSubCategories();
    }

    @DeleteMapping("/subcategories/delete")
    public boolean deleteSubCategory(@RequestParam String subCategoryId) {
        return adminService.deleteSubCategoryById(subCategoryId);
    }

    @PutMapping("/subcategories/status")
    public boolean updateSubCategoryStatus(@RequestParam String subCategoryId, @RequestParam Status status) {
        return adminService.updateSubCategoryStatus(subCategoryId, status);
    }

    // ================= BLOG =================

    @GetMapping("/blogs/all")
    public List<Blog> getAllBlogs() {
        return adminService.getAllBlogs();
    }

    @GetMapping("/blogs/details")
    public Blog getBlogById(@RequestParam String blogId) {
        return adminService.getBlogById(blogId);
    }

    @DeleteMapping("/blogs/delete")
    public boolean deleteBlog(@RequestParam String blogId) {
        return adminService.deleteBlogById(blogId);
    }

    @PutMapping("/blogs/status")
    public boolean updateBlogStatus(@RequestParam String blogId, @RequestParam Status status) {
        return adminService.updateBlogStatus(blogId, status);
    }
}
