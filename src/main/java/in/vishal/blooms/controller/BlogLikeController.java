package in.vishal.blooms.controller;

import in.vishal.blooms.security.JwtUtil;
import in.vishal.blooms.service.BlogLikeService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void likeOrUnlike(@RequestParam String blogId,
                             @RequestHeader("Authorization") String tokenHeader) {

        // "Bearer " ko hatake sirf token nikalenge
        String token = tokenHeader.substring(7);

        // Token se User ID nikalenge
        String userId = jwtUtil.extractUserId(token);

        // Service ko secure ID bhejenge
        blogLikeService.likeOrUnlike(blogId, userId);
    }
}