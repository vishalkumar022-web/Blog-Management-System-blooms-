package in.vishal.blooms.controller;

import in.vishal.blooms.security.JwtUtil;
import in.vishal.blooms.service.BlogCommentService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void addComment(@RequestParam String blogId,
                           @RequestParam String text,
                           @RequestHeader("Authorization") String tokenHeader) {

        // "Bearer " ko hatake sirf token nikalenge
        String token = tokenHeader.substring(7);

        // Token se User ID nikalenge
        String userId = jwtUtil.extractUserId(token);

        // Service ko secure ID bhejenge
        blogCommentService.addComment(blogId, userId, text);
    }
}