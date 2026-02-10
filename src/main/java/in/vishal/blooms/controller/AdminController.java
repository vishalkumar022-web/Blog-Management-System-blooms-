package in.vishal.blooms.controller;

import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.models.*;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/DetailsById")
    public ResponseEntity<ApiResponse<UserResponse>> getAdminById(@RequestParam String UserId) {
        return ResponseEntity.ok(adminService.getAdminById(UserId));
    }

    @GetMapping("/admins/Details")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAdmins() {
        return ResponseEntity.ok(adminService.getAdmins());
    }

    @PutMapping("/update/adminDetails")
    public ResponseEntity<ApiResponse<UserResponse>> updateAdmin(@RequestBody UserRequest adminRequest) {
        return ResponseEntity.ok(adminService.updateAdmin(adminRequest));
    }

    // ================= USERS (Fixed Pagination) =================
    @GetMapping("/users/all")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // ✅ Ab page aur size service ko pass ho raha hai
        return ResponseEntity.ok(adminService.getAllUsers(page, size));
    }

    @GetMapping("/users/user_id")
    public ResponseEntity<ApiResponse<User>> getUserById(@RequestParam String userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    @DeleteMapping("/usersById")
    public ResponseEntity<ApiResponse<Boolean>> deleteUser(@RequestParam String userId) {
        return ResponseEntity.ok(adminService.deleteUserById(userId));
    }

    // ================= CATEGORY (Fixed Pagination) =================
    @GetMapping("/categories/all")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminService.getAllCategories(page, size));
    }

    @GetMapping("/categories/category_id")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@RequestParam String categoryId) {
        return ResponseEntity.ok(adminService.getCategoryById(categoryId));
    }

    @DeleteMapping("/categoriesById")
    public ResponseEntity<ApiResponse<Boolean>> deleteCategory(@RequestParam String categoryId) {
        return ResponseEntity.ok(adminService.deleteCategoryById(categoryId));
    }

    @PutMapping("/categories/UpdateStatus")
    public ResponseEntity<ApiResponse<Boolean>> updateCategoryStatus(@RequestParam String categoryId, @RequestParam String status) {
        return ResponseEntity.ok(adminService.updateCategoryStatus(categoryId, status));
    }

    // ================= SUB CATEGORY (Fixed Pagination) =================
    @GetMapping("/subcategories/all")
    public ResponseEntity<ApiResponse<List<SubCategory>>> getAllSubCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminService.getAllSubCategories(page, size));
    }

    @GetMapping("/subcategories/subcategory_id")
    public ResponseEntity<ApiResponse<SubCategory>> getSubCategoryById(@RequestParam String subCategoryId) {
        return ResponseEntity.ok(adminService.getSubCategoryById(subCategoryId));
    }

    @DeleteMapping("/subcategoriesById")
    public ResponseEntity<ApiResponse<Boolean>> deleteSubCategory(@RequestParam String subCategoryId) {
        return ResponseEntity.ok(adminService.deleteSubCategoryById(subCategoryId));
    }

    @PutMapping("/subcategories/UpdateStatus")
    public ResponseEntity<ApiResponse<Boolean>> updateSubCategoryStatus(@RequestParam String subCategoryId, @RequestParam String status) {
        return ResponseEntity.ok(adminService.updateSubCategoryStatus(subCategoryId, status));
    }

    // ================= BLOG (Fixed Pagination) =================
    @GetMapping("/blogs/all")
    public ResponseEntity<ApiResponse<List<Blog>>> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminService.getAllBlogs(page, size));
    }

    @GetMapping("/blogs/blog_id")
    public ResponseEntity<ApiResponse<Blog>> getBlogById(@RequestParam String blogId) {
        return ResponseEntity.ok(adminService.getBlogById(blogId));
    }

    @DeleteMapping("/blogsById")
    public ResponseEntity<ApiResponse<Boolean>> deleteBlog(@RequestParam String blogId) {
        return ResponseEntity.ok(adminService.deleteBlogById(blogId));
    }

    @PutMapping("/blogs/UpdateStatus")
    public ResponseEntity<ApiResponse<Boolean>> updateBlogStatus(@RequestParam String blogId, @RequestParam String status) {
        return ResponseEntity.ok(adminService.updateBlogStatus(blogId, status));
    }
}