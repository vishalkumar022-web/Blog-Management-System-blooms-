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

<<<<<<< HEAD
    @GetMapping ("/admin/DetailsById")
    public UserResponse getAdminById(@RequestParam String UserId) {
        return adminService.getAdminById(UserId);
    }


    @GetMapping("/admins/Details")
=======
    // ================= ADMINS =================

    @GetMapping("/details") // ✅ Fixed URL
    public UserResponse getAdminById(@RequestParam String userId) {
        return adminService.getAdminById(userId);
    }

    @GetMapping("/list") // ✅ Fixed URL
>>>>>>> 8e3bb86d203d6edc6e889604ded05835a13ed44c
    public List<UserResponse> getAdmins() {
        return adminService.getAdmins();
    }

<<<<<<< HEAD

    @PutMapping("/update/adminDetails")
=======
    @PutMapping("/update") // ✅ Fixed URL
>>>>>>> 8e3bb86d203d6edc6e889604ded05835a13ed44c
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

<<<<<<< HEAD

    @PutMapping("/categories/UpdateStatus")
    public boolean updateCategoryStatus(@RequestParam String categoryId,
                                        @RequestParam String status) {

=======
    @PutMapping("/categories/status")
    public boolean updateCategoryStatus(@RequestParam String categoryId, @RequestParam Status status) {
>>>>>>> 8e3bb86d203d6edc6e889604ded05835a13ed44c
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

<<<<<<< HEAD

    @PutMapping("/subcategories/UpdateStatus")
    public boolean updateSubCategoryStatus(@RequestParam String subCategoryId,
                                           @RequestParam String status) {

=======
    @PutMapping("/subcategories/status")
    public boolean updateSubCategoryStatus(@RequestParam String subCategoryId, @RequestParam Status status) {
>>>>>>> 8e3bb86d203d6edc6e889604ded05835a13ed44c
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

<<<<<<< HEAD
    @PutMapping("/blogs/UpdateStatus")
    public boolean updateBlogStatus(@RequestParam String blogId,
                                    @RequestParam String status) {

=======
    @PutMapping("/blogs/status")
    public boolean updateBlogStatus(@RequestParam String blogId, @RequestParam Status status) {
>>>>>>> 8e3bb86d203d6edc6e889604ded05835a13ed44c
        return adminService.updateBlogStatus(blogId, status);
    }
}
