package in.vishal.blooms.service;

import in.vishal.blooms.dto.AdminLoginRequest;
import in.vishal.blooms.dto.AdminRequest;
import in.vishal.blooms.dto.AdminResponse;
import in.vishal.blooms.models.*;
import in.vishal.blooms.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {


    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public AdminService(AdminRepository adminRepository,
                        UserRepository userRepository,
                        BlogRepository blogRepository,
                        CategoryRepository categoryRepository,
                        SubCategoryRepository subCategoryRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    // Create Admin
    public void createAdmin(AdminRequest adminRequest) {

        Admin admin = new Admin();
        admin.setId(String.valueOf(System.currentTimeMillis()));
        admin.setAdminName(adminRequest.getAdminName());
        admin.setEmail(adminRequest.getEmail());
        admin.setName(adminRequest.getName());
        admin.setProfileUrl(adminRequest.getProfileUrl());
        admin.setPassword(adminRequest.getPassword());
        admin.setPhoneNumber(adminRequest.getPhoneNumber());


        adminRepository.save(admin);
    }

    // Get admin by id
    public AdminResponse getAdminById(String adminId) {

        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);

        if (optionalAdmin.isEmpty()) {
            return null;
        }

        Admin admin = optionalAdmin.get();


        AdminResponse response = new AdminResponse();
        response.setAdminId(admin.getId());
        response.setAdminName(admin.getAdminName());
        response.setEmail(admin.getEmail());
        response.setName(admin.getName());
        response.setProfileUrl(admin.getProfileUrl());
        response.setPhoneNumber(admin.getPhoneNumber());

        return response;
    }

    // Get all admins
    public List<AdminResponse> getAdmins() {

        List<Admin> adminList = adminRepository.findAll();
        List<AdminResponse> responses = new ArrayList<>();

        for (Admin admin : adminList) {

            AdminResponse response = new AdminResponse();
            response.setAdminId(admin.getId());
            response.setAdminName(admin.getAdminName());
            response.setEmail(admin.getEmail());
            response.setName(admin.getName());
            response.setProfileUrl(admin.getProfileUrl());
            response.setPassword(admin.getPassword());
            response.setPhoneNumber(admin.getPhoneNumber());
            responses.add(response);
        }

        return responses;
    }



    // Login admin
    public String loginAdmin(AdminLoginRequest loginRequest) {

        Admin admin = adminRepository.findByPhoneNumber(loginRequest.getPhoneNumber());

        if (admin == null) {
            return "Login Failed";
        }


        if (admin.getPassword().equals(loginRequest.getPassword())) {
            return "Login Successful";
        }

        return "Login Failed";
    }

    // Update admin
    public AdminResponse updateAdmin(AdminRequest adminRequest) {

        AdminResponse response = new AdminResponse();

        if (adminRequest.getAdminId() == null) {
            return response;
        }

        Optional<Admin> optionalAdmin = adminRepository.findById(adminRequest.getAdminId());

        Admin admin = optionalAdmin.get();

        admin.setAdminName(adminRequest.getAdminName());
        admin.setEmail(adminRequest.getEmail());
        admin.setName(adminRequest.getName());
        admin.setProfileUrl(adminRequest.getProfileUrl());
        admin.setPassword(adminRequest.getPassword());
        admin.setPhoneNumber(adminRequest.getPhoneNumber());

        adminRepository.save(admin);

        response.setAdminId(admin.getId());
        response.setAdminName(admin.getAdminName());
        response.setEmail(admin.getEmail());
        response.setName(admin.getName());
        response.setProfileUrl(admin.getProfileUrl());
        response.setPassword(admin.getPassword());

        return response;
    }


// ================= USERS =================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String userId) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return null;
        }

        return optionalUser.get();
    }

    public boolean deleteUserById(String userId) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();
        user.setActive(false);   // same as your USER delete logic
        userRepository.save(user);
        return true;
    }


// ================= CATEGORY =================

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(String categoryId) {

        Optional<Category> optionalCategory =
                categoryRepository.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            return null;
        }

        return optionalCategory.get();
    }

    public boolean deleteCategoryById(String categoryId) {

        Optional<Category> optionalCategory =
                categoryRepository.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            return false;
        }

        categoryRepository.deleteById(categoryId);
        return true;
    }



    public boolean updateCategoryStatus(String categoryId, String status) {

        Optional<Category> optionalCategory =
                categoryRepository.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            return false;
        }

        Category category = optionalCategory.get();
        category.setStatus(status);
        categoryRepository.save(category);

        return true;
    }




// ================= SUB CATEGORY =================

    public List<SubCategory> getAllSubCategories() {
        return subCategoryRepository.findAll();
    }

    public SubCategory getSubCategoryById(String subCategoryId) {

        Optional<SubCategory> optionalSubCategory =
                subCategoryRepository.findById(subCategoryId);

        if (optionalSubCategory.isEmpty()) {
            return null;
        }

        return optionalSubCategory.get();
    }

    public boolean deleteSubCategoryById(String subCategoryId) {

        Optional<SubCategory> optionalSubCategory =
                subCategoryRepository.findById(subCategoryId);

        if (optionalSubCategory.isEmpty()) {
            return false;
        }

        subCategoryRepository.deleteById(subCategoryId);
        return true;
    }


    public boolean updateSubCategoryStatus(String subCategoryId, String status) {

        Optional<SubCategory> optionalSubCategory =
                subCategoryRepository.findById(subCategoryId);

        if (optionalSubCategory.isEmpty()) {
            return false;
        }

        SubCategory subCategory = optionalSubCategory.get();
        subCategory.setStatus(status);
        subCategoryRepository.save(subCategory);

        return true;
    }


// ================= BLOG =================

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Blog getBlogById(String blogId) {

        Optional<Blog> optionalBlog =
                blogRepository.findById(blogId);

        if (optionalBlog.isEmpty()) {
            return null;
        }

        return optionalBlog.get();
    }

    public boolean deleteBlogById(String blogId) {

        Optional<Blog> optionalBlog =
                blogRepository.findById(blogId);

        if (optionalBlog.isEmpty()) {
            return false;
        }

        blogRepository.deleteById(blogId);
        return true;

    }



    public boolean updateBlogStatus(String blogId, String status) {

        Optional<Blog> optionalBlog = blogRepository.findById(blogId);

        if (optionalBlog.isEmpty()) {
            return false;
        }

        Blog blog = optionalBlog.get();
        blog.setStatus(status);
        blogRepository.save(blog);

        return true;
    }



}