package in.vishal.blooms.service;

import in.vishal.blooms.repository.BlogRepository;
import in.vishal.blooms.repository.CategoryMappingRepository;
import in.vishal.blooms.repository.CategoryRepository;
import in.vishal.blooms.repository.SubCategoryRepository;
import in.vishal.blooms.dto.BlogRequest;
import in.vishal.blooms.dto.BlogResponse;
import in.vishal.blooms.models.*;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final CategoryMappingRepository categoryMappingRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;


    public BlogService(BlogRepository blogRepository, CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository , CategoryMappingRepository categoryMappingRepository){
        this.blogRepository = blogRepository ;
        this.categoryMappingRepository = categoryMappingRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository ;

    }


    // Performing CRUD Operation here :---

    // Create a Blog here :--

    // Inside BlogService.java

public void createBlog(BlogRequest blogRequest) {

    // 1️⃣ Validate Category Exists
    if (!categoryRepository.existsById(blogRequest.getblogCategoryId())) {
        throw new RuntimeException("Invalid Category ID");
    }

    // 2️⃣ Validate SubCategory Exists & Belongs to Category
    // (Mapping table ki zarurat nahi hai)
    Optional<SubCategory> subCatOptional = subCategoryRepository.findById(blogRequest.getblogSubcategoryId());
    
    if (subCatOptional.isEmpty()) {
        throw new RuntimeException("Invalid SubCategory ID");
    }
    
    SubCategory subCategory = subCatOptional.get();
    // Check: Kya ye subcategory waqai ussi category ki hai?
    if (!subCategory.getCategoryId().equals(blogRequest.getblogCategoryId())) {
        throw new RuntimeException("SubCategory does not belong to the selected Category");
    }

    // 3️⃣ Create Blog
    Blog blog = new Blog();
    blog.setId(String.valueOf(System.currentTimeMillis()));
    blog.setTitle(blogRequest.getblogTitle());
    blog.setDescription(blogRequest.getblogDescription());
    blog.setContent(blogRequest.getblogContent());
    blog.setCategoryId(blogRequest.getblogCategoryId());
    blog.setSubcategoryId(blogRequest.getblogSubcategoryId());
    blog.setAuthorId(blogRequest.getblogAuthorId()); 

    blog.setActive(true);
    blog.setCreatedDTTM(LocalDateTime.now());
    blog.setStatus(Status.INREVIEW); 

    blogRepository.save(blog);
    System.out.println("✅ Blog created successfully");
}

    // read the blog :--
    public List<BlogResponse> getBlogs() {

        List<BlogResponse> responses = new ArrayList<>();

        List<Blog> blogList = blogRepository.findAll();
        for (Blog blog : blogList) {
            if (blog.getActive()) {
                BlogResponse blogResponse = new BlogResponse();
                blogResponse.setBlogId(blog.getId());
                blogResponse.setTitle(blog.getTitle());
                blogResponse.setDescription(blog.getDescription());
                blogResponse.setContent(blog.getContent());
                blogResponse.setAuthorId(blog.getAuthorId());
                blogResponse.setStatus(blogResponse.getStatus());




                // category & subcategory name nikalna
                Optional<Category>optionalCategory = categoryRepository.findById(blog.getCategoryId());
                if (optionalCategory.isPresent()) {
                    Category category = optionalCategory.get();
                    blogResponse.setCategoryName(category.getName());
                }
                Optional<SubCategory>optionalSubCategory = subCategoryRepository.findById(blog.getSubcategoryId());
                if (optionalSubCategory.isPresent()) {
                    SubCategory sc = optionalSubCategory.get();
                    blogResponse.setSubCategoryName(sc.getName());

                }

                responses.add(blogResponse);

            }
        }
        return responses;
    }

    // Update a Blog here

    public BlogResponse updateBlog(BlogRequest request) {

        //  Blog list lao
        Optional<Blog>optionalBlog = blogRepository.findById(request.getblogId());

        //  Blog ID match?
        if (optionalBlog.isEmpty()) {

            return null ;
        }
        Blog blog = optionalBlog.get();

        //  Update fields
        blog.setTitle(request.getblogTitle());
        blog.setDescription(request.getblogDescription());
        blog.setContent(request.getblogContent());
        blog.setCategoryId(request.getblogCategoryId());
        blog.setSubcategoryId(request.getblogSubcategoryId());

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
        Optional<Category>optionalCategory = categoryRepository.findById(blog.getCategoryId());
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            response.setCategoryName(category.getName());
        }
        Optional<SubCategory>optionalSubCategory = subCategoryRepository.findById(blog.getSubcategoryId());
        if (optionalSubCategory.isPresent()) {
            SubCategory sc = optionalSubCategory.get();
            response.setSubCategoryName(sc.getName());

        }

        return response;
    }

    // Delete a BLog here :--

    public boolean deleteBlog(String blogId) {

        Optional<Blog>optionalBlog = blogRepository.findById(blogId);

        if(optionalBlog.isEmpty()) {

            return false ;

        }
        Blog blog = optionalBlog.get();

        blog.setActive(false);// soft delete

        blogRepository.save(blog);
        return true;
    }

}



