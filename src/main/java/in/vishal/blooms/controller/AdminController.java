package in.vishal.blooms.controller;

import in.vishal.blooms.dto.AdminLoginRequest;
import in.vishal.blooms.dto.AdminRequest;
import in.vishal.blooms.dto.AdminResponse;
import in.vishal.blooms.models.Blog;
import in.vishal.blooms.models.Category;
import in.vishal.blooms.models.SubCategory;
import in.vishal.blooms.models.User;
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

    @GetMapping ("/admin DetailsById")
    public AdminResponse getAdminById(@RequestParam String AdminId) {
        return adminService.getAdminById(AdminId);
    }

    @GetMapping("/admins Details")
    public List<AdminResponse> getAdmins() {
        return adminService.getAdmins();
    }

    @PostMapping("/login")
    public String loginAdmin(@RequestBody AdminLoginRequest loginRequest) {
        return adminService.loginAdmin(loginRequest);
    }


    @PutMapping("/update adminDetails")
    public AdminResponse updateAdmin(@RequestBody AdminRequest adminRequest) {
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
                                        @RequestParam String status) {

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
                                           @RequestParam String status) {

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
                                    @RequestParam String status) {

        return adminService.updateBlogStatus(blogId, status);
    }

}
