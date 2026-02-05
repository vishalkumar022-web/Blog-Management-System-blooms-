package in.vishal.blooms.controller;

import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.models.*;
import in.vishal.blooms.service.AdminService;
import in.vishal.blooms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping ("/admin DetailsById")
    public UserResponse getAdminById(@RequestParam String UserId) {
        return adminService.getAdminById(UserId);
    }


    @GetMapping("/admins Details")
    public List<UserResponse> getAdmins() {
        return adminService.getAdmins();
    }


    @PutMapping("/update adminDetails")
    public UserResponse updateAdmin(@RequestBody UserRequest adminRequest) {
        return adminService.updateAdmin(adminRequest);
    }



    // ================= USERS =================

    @GetMapping("/users/all")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/users/user_id")
    public User getUserById(@RequestParam String userId) {
        return adminService.getUserById(userId);
    }

    @DeleteMapping("/usersById")
    public boolean deleteUser(@RequestParam String userId) {
        return adminService.deleteUserById(userId);
    }

    // ================= CATEGORY =================

    @GetMapping("/categories/all")
    public List<Category> getAllCategories() {
        return adminService.getAllCategories();
    }

    @GetMapping("/categories/category_id")
    public Category getCategoryById(@RequestParam String categoryId) {
        return adminService.getCategoryById(categoryId);
    }

    @DeleteMapping("/categoriesById")
    public boolean deleteCategory(@RequestParam String categoryId) {
        return adminService.deleteCategoryById(categoryId);
    }


    @PutMapping("/categories/UpdateStatus")
    public boolean updateCategoryStatus(@RequestParam String categoryId,
                                        @RequestParam Status status) {

        return adminService.updateCategoryStatus(categoryId, status);
    }



    // ================= SUB CATEGORY =================

    @GetMapping("/subcategories/all")
    public List<SubCategory> getAllSubCategories() {
        return adminService.getAllSubCategories();
    }

    @GetMapping("/subcategories/subcategory_id")
    public SubCategory getSubCategoryById(@RequestParam String subCategoryId) {
        return adminService.getSubCategoryById(subCategoryId);
    }

    @DeleteMapping("/subcategoriesById")
    public boolean deleteSubCategory(@RequestParam String subCategoryId) {
        return adminService.deleteSubCategoryById(subCategoryId);
    }


    @PutMapping("/subcategories/UpdateStatus")
    public boolean updateSubCategoryStatus(@RequestParam String subCategoryId,
                                           @RequestParam Status status) {

        return adminService.updateSubCategoryStatus(subCategoryId, status);
    }





    // ================= BLOG =================

    @GetMapping("/blogs/all")
    public List<Blog> getAllBlogs() {
        return adminService.getAllBlogs();
    }

    @GetMapping("/blogs/blog_id")
    public Blog getBlogById(@RequestParam String blogId) {
        return adminService.getBlogById(blogId);
    }

    @DeleteMapping("/blogsById")
    public boolean deleteBlog(@RequestParam String blogId) {
        return adminService.deleteBlogById(blogId);
    }

    @PutMapping("/blogs/UpdateStatus")
    public boolean updateBlogStatus(@RequestParam String blogId,
                                    @RequestParam Status status) {

        return adminService.updateBlogStatus(blogId, status);
    }

}
