package in.vishal.blooms.controller;

import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.security.JwtUtil;
import in.vishal.blooms.service.BlogCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blog/comment")
public class BlogCommentController {

    private final BlogCommentService blogCommentService;

    @Autowired
    private JwtUtil jwtUtil;

    public BlogCommentController(BlogCommentService blogCommentService) {
        this.blogCommentService = blogCommentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addComment(@RequestParam String blogId,
                                                          @RequestParam String text,
                                                          @RequestHeader("Authorization") String tokenHeader) {

        String token = tokenHeader.substring(7);
        String userId = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(blogCommentService.addComment(blogId, userId, text));
    }
}