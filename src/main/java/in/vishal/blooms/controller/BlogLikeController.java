package in.vishal.blooms.controller;

import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.security.JwtUtil;
import in.vishal.blooms.service.BlogLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blog/like")
public class BlogLikeController {

    private final BlogLikeService blogLikeService;

    @Autowired
    private JwtUtil jwtUtil;

    public BlogLikeController(BlogLikeService blogLikeService) {
        this.blogLikeService = blogLikeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> likeOrUnlike(@RequestParam String blogId,
                                                            @RequestHeader("Authorization") String tokenHeader) {

        String token = tokenHeader.substring(7);
        String userId = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(blogLikeService.likeOrUnlike(blogId, userId));
    }
}