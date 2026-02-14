package in.vishal.blooms.service;

import in.vishal.blooms.dto.CommentResponse;
import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.repository.*;
import in.vishal.blooms.dto.BlogRequest;
import in.vishal.blooms.dto.BlogResponse;
import in.vishal.blooms.models.*;
import in.vishal.blooms.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BlogService {
    private static final Logger log = LoggerFactory.getLogger(BlogService.class);
    private final UserRepository userRepository;
    private final BlogLikeRepository blogLikeRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final BlogRepository blogRepository;
    private final CategoryMappingRepository categoryMappingRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public BlogService(BlogRepository blogRepository, CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository, CategoryMappingRepository categoryMappingRepository, BlogCommentRepository blogCommentRepository, BlogLikeRepository blogLikeRepository, UserRepository userRepository) {
        this.blogRepository = blogRepository;
        this.categoryMappingRepository = categoryMappingRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.blogLikeRepository = blogLikeRepository;
        this.blogCommentRepository = blogCommentRepository;
        this.userRepository = userRepository;
    }

    // Performing CRUD Operation here :---

    // Create a Blog here :--
    @Caching(evict = {
            @CacheEvict(value = "blogs", allEntries = true),
            @CacheEvict(value = "users", allEntries = true) // ✅ Cache Clean: User profile refresh hoga
    })
    public ApiResponse<String> createBlog(BlogRequest blogRequest) {
        log.info("Creating blog with title: {}", blogRequest.getBlogTitle());
        boolean categoryExists = false;

        Optional<Category> optionalCategory = categoryRepository.findById(blogRequest.getBlogCategoryId());

        if (optionalCategory.isPresent()) {
            categoryExists = true;
        }

        if (!categoryExists) {
            System.out.println("Invalid category");
            throw new ApplicationException("Invalid category ID: " + blogRequest.getBlogCategoryId());
        }

        // STEP 2 CategoryMapping se subcategory validate karo
        CategoryMapping matchedMapping = null;
        Optional<CategoryMapping> optionalCategoryMapping = categoryMappingRepository.findById(blogRequest.getBlogCategoryId());

        if (optionalCategoryMapping.isPresent()) {
            matchedMapping = optionalCategoryMapping.get();
        }

        if (matchedMapping == null) {
            System.out.println("No subcategories exist for this category because this category id is not exist in categoryMapping list");
            throw new ApplicationException("No subcategories exist for this category because this category id " + blogRequest.getBlogCategoryId() + "is not exist in categoryMapping list");
        }

        // STEP 3️ SubCategoryId valid hai ya nahi
        boolean subCategoryValid = false;
        if (matchedMapping.getSubCategoryIdsList().contains(blogRequest.getBlogSubcategoryId())) {
            subCategoryValid = true;
        }

        if (!subCategoryValid) {
            System.out.println("Invalid subcategory for this category");
            throw new ApplicationException("invalid subcategory ID: " + blogRequest.getBlogSubcategoryId() + " for category ID: " + blogRequest.getBlogCategoryId());
        }

        try {
            Blog blog = new Blog();

            blog.setTitle(blogRequest.getBlogTitle());
            blog.setDescription(blogRequest.getBlogDescription());
            blog.setContent(blogRequest.getBlogContent());
            blog.setCategoryId(blogRequest.getBlogCategoryId());
            blog.setSubcategoryId(blogRequest.getBlogSubcategoryId());

            // ✅ CRITICAL FIX: Author ID ab Token wale User ID se set hoga (frontend pe bharosa nahi)
            blog.setAuthorId(blogRequest.getUserId());

            blog.setActive(true);
            blog.setCreatedDTTM(LocalDateTime.now());
            blog.setStatus(Status.INREVIEW.getDisplayName());

            blog.setId(String.valueOf(System.currentTimeMillis()));

            // CreatedBy bhi Token ID se set hoga
            blog.setCreatedBy(blogRequest.getUserId());

            blogRepository.save(blog);
            log.info("Blog created successfully with ID: {}", blog.getId());
            return new ApiResponse<>(true, "Blog created successfully with ID: " + blog.getId(), null);

        } catch (Exception e) {
            log.error("Error creating blog: {}", e.getMessage());
            throw new ApplicationException("Error creating blog: " + e.getMessage());
        }
    }

    // read the blog :--
    @Cacheable(value = "blogs", key = "#page + '-' + #size")
    public ApiResponse<List<BlogResponse>> getBlogs(int page, int size) {
        log.info("Fetching blogs for page: {}, size: {}", page, size);

        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("title").ascending());
            Page<Blog> blogPage = blogRepository.findAll(pageRequest);
            List<Blog> blogList = blogPage.getContent();

            List<BlogResponse> responses = new ArrayList<>();

            for (Blog blog : blogList) {
                if (!blog.getActive()) { continue; }

                BlogResponse blogResponse = new BlogResponse();
                blogResponse.setBlogId(blog.getId());
                blogResponse.setTitle(blog.getTitle());
                blogResponse.setDescription(blog.getDescription());
                blogResponse.setContent(blog.getContent());
                blogResponse.setAuthorId(blog.getAuthorId());
                blogResponse.setStatus(blog.getStatus());

                Optional<Category> optionalCategory = categoryRepository.findById(blog.getCategoryId());
                if (optionalCategory.isPresent()) {
                    Category category = optionalCategory.get();
                    blogResponse.setCategoryName(category.getName());
                }

                Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(blog.getSubcategoryId());
                if (optionalSubCategory.isPresent()) {
                    SubCategory subCategory = optionalSubCategory.get();
                    blogResponse.setSubCategoryName(subCategory.getName());
                }

                List<String> likedUserNames = new ArrayList<>();
                List<BlogLike> blogLikes = blogLikeRepository.findByBlogId(blog.getId());

                for (BlogLike blogLike : blogLikes) {
                    Optional<User> optionalUser = userRepository.findById(blogLike.getUserId());
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        likedUserNames.add(user.getUserName());
                    }
                }
                blogResponse.setLikedByUsers(likedUserNames);
                blogResponse.setLikeCount(likedUserNames.size());

                List<CommentResponse> commentResponses = new ArrayList<>();
                List<BlogComment> comments = blogCommentRepository.findByBlogId(blog.getId());

                for (BlogComment comment : comments) {
                    CommentResponse cr = new CommentResponse();
                    cr.setCommentText(comment.getCommentText());
                    Optional<User> optionalUser = userRepository.findById(comment.getUserId());
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        cr.setUserName(user.getUserName());
                    }
                    commentResponses.add(cr);
                }
                blogResponse.setComments(commentResponses);
                blogResponse.setCommentCount(commentResponses.size());
                responses.add(blogResponse);
            }
            return new ApiResponse<>(true, "Blogs fetched successfully", responses);

        } catch (Exception e) {
            log.error("Error fetching blogs: {}", e.getMessage());
            throw new ApplicationException("Error fetching blogs");
        }
    }

    // PUBLIC SEARCH BLOG
    public ApiResponse<List<BlogResponse>> searchBlogsByTitle(String title, int page, int size) {
        log.info("Searching blogs with title containing: {}, page: {}, size: {}", title, page, size);

        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("title").ascending());
            List<BlogResponse> responses = new ArrayList<>();

            Page<Blog> blogPage = blogRepository.findByTitleContainingIgnoreCaseAndStatusIgnoreCaseAndActive(
                    title.trim(), Status.PUBLISHED.getDisplayName(), true, pageRequest);

            List<Blog> blogList = blogPage.getContent();

            for (Blog blog : blogList) {
                BlogResponse blogResponse = new BlogResponse();
                blogResponse.setBlogId(blog.getId());
                blogResponse.setTitle(blog.getTitle());
                blogResponse.setDescription(blog.getDescription());
                blogResponse.setContent(blog.getContent());
                blogResponse.setAuthorId(blog.getAuthorId());

                Optional<Category> optionalCategory = categoryRepository.findById(blog.getCategoryId());
                if (optionalCategory.isPresent()) {
                    Category category = optionalCategory.get();
                    blogResponse.setCategoryName(category.getName());
                }

                Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(blog.getSubcategoryId());
                if (optionalSubCategory.isPresent()) {
                    SubCategory sc = optionalSubCategory.get();
                    blogResponse.setSubCategoryName(sc.getName());
                }
                responses.add(blogResponse);
            }
            return new ApiResponse<>(true, "Search results fetched", responses);

        } catch (Exception e) {
            log.error("Error searching blogs: {}", e.getMessage());
            throw new ApplicationException("Error during blog search");
        }
    }

    // Update a Blog here
    @Caching(evict = {
            @CacheEvict(value = "blogs", allEntries = true),
            @CacheEvict(value = "users", allEntries = true) // ✅ Cache Clean
    })
    public ApiResponse<BlogResponse> updateBlog(BlogRequest request) {
        log.info("Updating blog ID: {}", request.getBlogId());

        Optional<Blog> optionalBlog = blogRepository.findById(request.getBlogId());
        if (optionalBlog.isEmpty()) {
            throw new ApplicationException("Blog not found with ID: " + request.getBlogId());
        }

        try {
            Blog blog = optionalBlog.get();
            blog.setTitle(request.getBlogTitle());
            blog.setDescription(request.getBlogDescription());
            blog.setContent(request.getBlogContent());
            blog.setCategoryId(request.getBlogCategoryId());
            blog.setSubcategoryId(request.getBlogSubcategoryId());

            blogRepository.save(blog);

            BlogResponse response = new BlogResponse();
            response.setBlogId(blog.getId());
            response.setTitle(blog.getTitle());
            response.setDescription(blog.getDescription());
            response.setContent(blog.getContent());
            response.setAuthorId(blog.getAuthorId());

            Optional<Category> optionalCategory = categoryRepository.findById(blog.getCategoryId());
            if (optionalCategory.isPresent()) {
                Category category = optionalCategory.get();
                response.setCategoryName(category.getName());
            }
            Optional<SubCategory> optionalSubCategory = subCategoryRepository.findById(blog.getSubcategoryId());
            if (optionalSubCategory.isPresent()) {
                SubCategory sc = optionalSubCategory.get();
                response.setSubCategoryName(sc.getName());
            }
            return new ApiResponse<>(true, "Blog updated successfully", response);

        } catch (Exception e) {
            log.error("Update failed: {}", e.getMessage());
            throw new ApplicationException("Failed to update blog");
        }
    }

    //DELETE BLOG
    @Caching(evict = {
            @CacheEvict(value = "blogs", allEntries = true),
            @CacheEvict(value = "users", allEntries = true) // ✅ Cache Clean
    })
    public ApiResponse<Boolean> deleteBlog(String blogId) {
        log.info("Deleting blog ID: {}", blogId);

        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isEmpty()) {
            throw new ApplicationException("Blog not found for deletion");
        }

        try {
            Blog blog = optionalBlog.get();
            blog.setActive(false);
            blogRepository.save(blog);
            return new ApiResponse<>(true, "Blog deleted successfully", true);
        } catch (Exception e) {
            log.error("Delete failed: {}", e.getMessage());
            throw new ApplicationException("Failed to delete blog");
        }
    }
}