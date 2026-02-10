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

        // STEP 2 CategoryMapping se subcategory validate karo ( MAIN CHANGE)
        CategoryMapping matchedMapping = null;

        Optional<CategoryMapping> optionalCategoryMapping = categoryMappingRepository.findById(blogRequest.getBlogCategoryId());

        if (optionalCategoryMapping.isPresent()) {
            matchedMapping = optionalCategoryMapping.get(); // ye category id store ho gya matchedMapping me

        }


        if (matchedMapping == null) {
            System.out.println("No subcategories exist for this category because this category id is not exist in categoryMapping list");
            throw new ApplicationException("No subcategories exist for this category because this category id " + blogRequest.getBlogCategoryId() + "is not exist in categoryMapping list");
        }

        // STEP 3️ SubCategoryId valid hai ya nahi (mapping ke andar)
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
            blog.setAuthorId(blogRequest.getBlogAuthorId());

            blog.setActive(true);
            blog.setCreatedDTTM(LocalDateTime.now());
            blog.setStatus(Status.INREVIEW.getDisplayName());

            blog.setId(String.valueOf(System.currentTimeMillis()));

            blogRepository.save(blog);
            log.info("Blog created successfully with ID: {}", blog.getId());
            return new ApiResponse<>(true, "Blog created successfully with ID: " + blog.getId(), null);


        } catch (Exception e) {
            log.error("Error creating blog: {}", e.getMessage());
            throw new ApplicationException("Error creating blog: " + e.getMessage());
        }
    }

    // read the blog :--
    public ApiResponse<List<BlogResponse>> getBlogs(int page, int size) {
        log.info("Fetching blogs for page: {}, size: {}", page, size);

        try {
            // ✅ Pagination Setup (Sort by Name)
            // Ab DB se sirf 'size' amount ka data aayega (e.g., 10 records)
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("title").ascending());

            Page<Blog> blogPage = blogRepository.findAll(pageRequest);
            List<Blog> blogList = blogPage.getContent(); // List nikali

            List<BlogResponse> responses = new ArrayList<>();


            for (Blog blog : blogList) {

                if (!blog.getActive()) {
                    continue;
                }

                BlogResponse blogResponse = new BlogResponse();
                blogResponse.setBlogId(blog.getId());
                blogResponse.setTitle(blog.getTitle());
                blogResponse.setDescription(blog.getDescription());
                blogResponse.setContent(blog.getContent());
                blogResponse.setAuthorId(blog.getAuthorId());
                blogResponse.setStatus(blog.getStatus());

                // ===== CATEGORY NAME =====
                Optional<Category> optionalCategory =
                        categoryRepository.findById(blog.getCategoryId());

                if (optionalCategory.isPresent()) {
                    Category category = optionalCategory.get();
                    blogResponse.setCategoryName(category.getName());
                }

                // ===== SUBCATEGORY NAME =====
                Optional<SubCategory> optionalSubCategory =
                        subCategoryRepository.findById(blog.getSubcategoryId());

                if (optionalSubCategory.isPresent()) {
                    SubCategory subCategory = optionalSubCategory.get();
                    blogResponse.setSubCategoryName(subCategory.getName());
                }

                // ===== LIKES =====
                List<String> likedUserNames = new ArrayList<>();

                List<BlogLike> blogLikes =
                        blogLikeRepository.findByBlogId(blog.getId());

                for (BlogLike blogLike : blogLikes) {

                    Optional<User> optionalUser =
                            userRepository.findById(blogLike.getUserId());

                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        likedUserNames.add(user.getUserName());
                    }
                }

                blogResponse.setLikedByUsers(likedUserNames);
                blogResponse.setLikeCount(likedUserNames.size());

                // ===== COMMENTS =====
                List<CommentResponse> commentResponses = new ArrayList<>();

                List<BlogComment> comments =
                        blogCommentRepository.findByBlogId(blog.getId());

                for (BlogComment comment : comments) {

                    CommentResponse cr = new CommentResponse();
                    cr.setCommentText(comment.getCommentText());

                    Optional<User> optionalUser =
                            userRepository.findById(comment.getUserId());

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


    // PUBLIC SEARCH BLOG (only PUBLISHED blogs)
    public ApiResponse<List<BlogResponse>> searchBlogsByTitle(String title, int page, int size) {
        log.info("Searching blogs with title containing: {}, page: {}, size: {}", title, page, size);

        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("title").ascending());

            List<BlogResponse> responses = new ArrayList<>();

            Page<Blog> blogPage = blogRepository.findByTitleContainingIgnoreCaseAndStatusAndActive(title, Status.PUBLISHED.getDisplayName(), true, pageRequest);

            List<Blog> blogList = blogPage.getContent();

            for (Blog blog : blogList) {

                BlogResponse blogResponse = new BlogResponse();

                blogResponse.setBlogId(blog.getId());
                blogResponse.setTitle(blog.getTitle());
                blogResponse.setDescription(blog.getDescription());
                blogResponse.setContent(blog.getContent());
                blogResponse.setAuthorId(blog.getAuthorId());

                // category name nikalna
                Optional<Category> optionalCategory =
                        categoryRepository.findById(blog.getCategoryId());

                if (optionalCategory.isPresent()) {
                    Category category = optionalCategory.get();
                    blogResponse.setCategoryName(category.getName());
                }

                // subcategory name nikalna
                Optional<SubCategory> optionalSubCategory =
                        subCategoryRepository.findById(blog.getSubcategoryId());

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


    public ApiResponse<BlogResponse> updateBlog(BlogRequest request) {
        log.info("Updating blog ID: {}", request.getBlogId());

        Optional<Blog> optionalBlog = blogRepository.findById(request.getBlogId());
        if (optionalBlog.isEmpty()) {
            throw new ApplicationException("Blog not found with ID: " + request.getBlogId());
        }

        //  Update fields
        try {
            Blog blog = optionalBlog.get();
            blog.setTitle(request.getBlogTitle());
            blog.setDescription(request.getBlogDescription());
            blog.setContent(request.getBlogContent());
            blog.setCategoryId(request.getBlogCategoryId());
            blog.setSubcategoryId(request.getBlogSubcategoryId());

            blogRepository.save(blog);

            //  Response banao
            BlogResponse response = new BlogResponse();
            response.setBlogId(blog.getId());
            response.setTitle(blog.getTitle());
            response.setDescription(blog.getDescription());
            response.setContent(blog.getContent());
            response.setAuthorId(blog.getAuthorId());

            // 6️ Category / SubCategory name nikalna


            // category & subcategory name nikalna
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
    // Delete a BLog here :--

    //DELETE BLOG
    // ==========================================
    public ApiResponse<Boolean> deleteBlog(String blogId) {
        log.info("Deleting blog ID: {}", blogId);

        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isEmpty()) {
            throw new ApplicationException("Blog not found for deletion");
        }

        try {
            Blog blog = optionalBlog.get();
            blog.setActive(false); // Soft delete
            blogRepository.save(blog);
            return new ApiResponse<>(true, "Blog deleted successfully", true);
        } catch (Exception e) {
            log.error("Delete failed: {}", e.getMessage());
            throw new ApplicationException("Failed to delete blog");
        }
    }
}
