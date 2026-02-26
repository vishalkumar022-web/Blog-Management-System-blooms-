package in.vishal.blooms.controller;

import in.vishal.blooms.dto.BlogRequest;
import in.vishal.blooms.dto.BlogResponse;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.security.JwtUtil;
import in.vishal.blooms.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createBlog(@RequestBody BlogRequest blogRequest) {
        return ResponseEntity.ok(blogService.createBlog(blogRequest));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(blogService.getBlogs(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> searchBlogs(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(blogService.searchBlogsByTitle(title, page, size));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<BlogResponse>> updateBlog(@RequestBody BlogRequest request , @RequestHeader ("Authorization") String tokenHeader) {
        String token = tokenHeader.substring(7);
        String userIdFromToken = jwtUtil.extractUserId(token);
        request.setUserId(userIdFromToken);
        return ResponseEntity.ok(blogService.updateBlog(request));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Boolean>> deleteBlog(@RequestHeader ("Authorization") String tokenHeader , @RequestParam String blogId) {
        String token = tokenHeader.substring(7);
        String userIdFromToken = jwtUtil.extractUserId(token);
        // ✅ FIXED: Backend me ID bhejna zaroori hai hakker se bachne ke liye
        return ResponseEntity.ok(blogService.deleteBlog(blogId, userIdFromToken));
    }
}