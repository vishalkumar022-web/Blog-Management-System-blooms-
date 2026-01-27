package in.vishal.blooms.controller;


import in.vishal.blooms.dto.BlogRequest;
import in.vishal.blooms.dto.BlogResponse;
import in.vishal.blooms.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/BLog")
public class BlogController {

    @Autowired
    private  BlogService blogService ;





    // Performing CRUD Operation here :---
    @PostMapping
    public void createBlog(@RequestBody BlogRequest blogRequest){

        blogService.createBlog(blogRequest);

    }

    // read the blog :--
    @GetMapping
    public List<BlogResponse> getBlogs() {

        return blogService.getBlogs();
    }
    @PutMapping
    public BlogResponse updateBlog(@RequestBody BlogRequest request) {

        return blogService.updateBlog(request);

    }

    @DeleteMapping
    public boolean deleteBlog(@RequestParam String blogId) {

        return blogService.deleteBlog(blogId);
    }

}
