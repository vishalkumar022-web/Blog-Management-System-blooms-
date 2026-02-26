package in.vishal.blooms.service;

import in.vishal.blooms.dto.UserRequest;
import in.vishal.blooms.dto.UserResponse;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.*;
import in.vishal.blooms.repository.*;
import in.vishal.blooms.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public AdminService(UserRepository userRepository,
                        BlogRepository blogRepository,
                        CategoryRepository categoryRepository,
                        SubCategoryRepository subCategoryRepository) {

        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    // Helper Method
    private boolean isValidStatus(String status) {
        for (Status s : Status.values()) {
            if (s.getDisplayName().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    // Get Admin by ID
    public ApiResponse<UserResponse> getAdminById(String userId) {
        log.info("Fetching Admin details by ID: {}", userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) throw new ApplicationException("Admin user not found");

        User user = userOptional.get();
        if (!user.isActive()) throw new ApplicationException("User inactive");

        if(!user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName())) {
            return new ApiResponse<>(false, "Role mismatch", null);
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setProfileUrl(user.getProfileUrl());
        userResponse.setName(user.getName());
        userResponse.setUserName(user.getUserName());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setRole(user.getRole());

        return new ApiResponse<>(true, "Admin details fetched", userResponse);
    }

    // Get All Admins
    public ApiResponse<List<UserResponse>> getAdmins() {
        log.info("Fetching all admins");
        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();

        for (User user : userList) {
            if (user.isActive() && user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName())) {
                UserResponse userResponse = new UserResponse();
                userResponse.setUserId(user.getId());
                userResponse.setEmail(user.getEmail());
                userResponse.setProfileUrl(user.getProfileUrl());
                userResponse.setName(user.getName());
                userResponse.setUserName(user.getUserName());
                userResponse.setPhoneNumber(user.getPhoneNumber());
                userResponses.add(userResponse);
            }
        }
        return new ApiResponse<>(true, "Admins fetched successfully", userResponses);
    }

    // Update Admin
    public ApiResponse<UserResponse> updateAdmin(UserRequest userRequest) {
        log.info("Updating admin: {}", userRequest.getUserId());
        if (userRequest.getUserId() == null) throw new ApplicationException("User ID is required");

        Optional<User> optionalUser = userRepository.findById(userRequest.getUserId());
        if(optionalUser.isEmpty()) throw new ApplicationException("User not found");

        User user = optionalUser.get();

        if (user.getRole().equalsIgnoreCase(Role.ADMIN.getDisplayName())) {
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());
            user.setUserName(userRequest.getUserName());
            user.setProfileUrl(userRequest.getProfileUrl());
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setRole(userRequest.getRole());
            userRepository.save(user);
        } else {
            throw new ApplicationException("User is not an Admin");
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setUserName(user.getUserName());
        userResponse.setUserId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setProfileUrl(user.getProfileUrl());
        return new ApiResponse<>(true, "Admin updated successfully", userResponse);
    }

    // ================= USERS (Paginated) =================
    public ApiResponse<List<User>> getAllUsers(int page, int size) {
        log.info("Fetching all users page: {}", page);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<User> userPage = userRepository.findAll(pageRequest);
        return new ApiResponse<>(true, "Users fetched", userPage.getContent());
    }

    public ApiResponse<User> getUserById(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) throw new ApplicationException("User not found");
        return new ApiResponse<>(true, "User fetched", optionalUser.get());
    }

    public ApiResponse<Boolean> deleteUserById(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) throw new ApplicationException("User not found");
        User user = optionalUser.get();
        user.setActive(false);
        userRepository.save(user);
        return new ApiResponse<>(true, "User deleted", true);
    }

    // ================= CATEGORY (Paginated) =================
    public ApiResponse<List<Category>> getAllCategories(int page, int size) {
        log.info("Fetching all categories page: {}", page);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Category> pageList = categoryRepository.findAll(pageRequest);
        return new ApiResponse<>(true, "Categories fetched", pageList.getContent());
    }

    public ApiResponse<Category> getCategoryById(String categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if(optionalCategory.isEmpty()) throw new ApplicationException("Category not found");
        return new ApiResponse<>(true, "Category fetched", optionalCategory.get());
    }

    public ApiResponse<Boolean> deleteCategoryById(String categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) throw new ApplicationException("Category not found");
        categoryRepository.deleteById(categoryId);
        return new ApiResponse<>(true, "Category deleted", true);
    }
    @CacheEvict(value = "category", allEntries = true) // ✅ Ye line jadu hai! Isse purana cache delete ho jayega
    public ApiResponse<Boolean> updateCategoryStatus(String categoryId, String status) {
        // FIXED: Added '!' to check if status is NOT valid
        if (!isValidStatus(status)) throw new ApplicationException("Invalid Status Provided: " + status);

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) throw new ApplicationException("Category not found");

        Category category = optionalCategory.get();
        category.setStatus(status);
        categoryRepository.save(category);
        return new ApiResponse<>(true, "Category status updated", true);
    }

    // ================= SUB CATEGORY (Paginated) =================
    public ApiResponse<List<SubCategory>> getAllSubCategories(int page, int size) {
        log.info("Fetching subcategories page: {}", page);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<SubCategory> pageList = subCategoryRepository.findAll(pageRequest);
        return new ApiResponse<>(true, "Subcategories fetched", pageList.getContent());
    }

    public ApiResponse<SubCategory> getSubCategoryById(String subCategoryId) {
        Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(subCategoryId);
        if(optionalSubCategory.isEmpty()) throw new ApplicationException("SubCategory not found");
        return new ApiResponse<>(true, "SubCategory fetched", optionalSubCategory.get());
    }

    public ApiResponse<Boolean> deleteSubCategoryById(String subCategoryId) {
        Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(subCategoryId);
        if (optionalSubCategory.isEmpty()) throw new ApplicationException("SubCategory not found");
        subCategoryRepository.deleteById(subCategoryId);
        return new ApiResponse<>(true, "SubCategory deleted", true);
    }
    @CacheEvict(value = "subcategory", allEntries = true) // ✅ Ye line jadu hai! Isse purana cache delete ho jayega
    public ApiResponse<Boolean> updateSubCategoryStatus(String subCategoryId, String status) {
        // FIXED: Added '!' to check if status is NOT valid
        if (!isValidStatus(status)) throw new ApplicationException("Invalid Status Provided: " + status);

        Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(subCategoryId);
        if (optionalSubCategory.isEmpty()) throw new ApplicationException("SubCategory not found");

        SubCategory subCategory = optionalSubCategory.get();
        subCategory.setStatus(status);
        subCategoryRepository.save(subCategory);
        return new ApiResponse<>(true, "SubCategory status updated", true);
    }

    // ================= BLOG (Paginated) =================
    public ApiResponse<List<Blog>> getAllBlogs(int page, int size) {
        log.info("Fetching blogs page: {}", page);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("title").ascending());
        Page<Blog> pageList = blogRepository.findAll(pageRequest);
        return new ApiResponse<>(true, "Blogs fetched", pageList.getContent());
    }

    public ApiResponse<Blog> getBlogById(String blogId) {
        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if(optionalBlog.isEmpty()) throw new ApplicationException("Blog not found");
        return new ApiResponse<>(true, "Blog fetched", optionalBlog.get());
    }

    public ApiResponse<Boolean> deleteBlogById(String blogId) {
        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isEmpty()) throw new ApplicationException("Blog not found");
        blogRepository.deleteById(blogId);
        return new ApiResponse<>(true, "Blog deleted", true);
    }
    @CacheEvict(value = "blogs", allEntries = true) // ✅ Ye line jadu hai! Isse purana cache delete ho jayega
    public ApiResponse<Boolean> updateBlogStatus(String blogId, String status) {
        // FIXED: Added '!' to check if status is NOT valid
        if (!isValidStatus(status)) throw new ApplicationException("Invalid Status Provided: " + status);

        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isEmpty()) throw new ApplicationException("Blog not found");

        Blog blog = optionalBlog.get();
        blog.setStatus(status);
        blogRepository.save(blog);
        return new ApiResponse<>(true, "Blog status updated", true);
    }
}