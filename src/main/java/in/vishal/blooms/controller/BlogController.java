package in.vishal.blooms.controller;

import in.vishal.blooms.dto.BlogRequest;
import in.vishal.blooms.dto.BlogResponse;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.security.JwtUtil;
import in.vishal.blooms.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/BLog")
public class BlogController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BlogService blogService;

    // 1. Create Blog
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createBlog(@RequestBody BlogRequest blogRequest) {
        // Service ApiResponse return kar raha hai, bas ResponseEntity me wrap karna hai
        return ResponseEntity.ok(blogService.createBlog(blogRequest));
    }

    // 2. Get All Blogs (With Pagination)
    // URL Example: /api/BLog?page=0&size=10
    @GetMapping
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(blogService.getBlogs(page, size));
    }

    // 3. Search Blogs (With Pagination)
    // URL Example: /api/BLog/search?title=java&page=0&size=10
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> searchBlogs(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(blogService.searchBlogsByTitle(title, page, size));
    }

    // 4. Update Blog
    @PutMapping
    public ResponseEntity<ApiResponse<BlogResponse>> updateBlog(@RequestBody BlogRequest request , @RequestHeader ("Authorization") String tokenHeader) {
        String token = tokenHeader.substring(7);
        String userIdFromToken = jwtUtil.extractUserId(token);
        request.setUserId(userIdFromToken);
        return ResponseEntity.ok(blogService.updateBlog(request));
    }

    // 5. Delete Blog
    @DeleteMapping
    public ResponseEntity<ApiResponse<Boolean>> deleteBlog(@RequestHeader ("Authorization") String tokenHeader , @RequestParam String blogId) {
        String token = tokenHeader.substring(7);
        String userIdFromToken = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(blogService.deleteBlog(blogId));
    }
}