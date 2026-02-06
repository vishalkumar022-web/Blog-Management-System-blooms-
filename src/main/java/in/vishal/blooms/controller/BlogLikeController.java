package in.vishal.blooms.controller;

import in.vishal.blooms.service.BlogLikeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blog/like")
public class BlogLikeController {

    private final BlogLikeService blogLikeService;

    public BlogLikeController(BlogLikeService blogLikeService) {
        this.blogLikeService = blogLikeService;
    }

    @PostMapping
    public void likeOrUnlike(@RequestParam String blogId,
                             @RequestParam String userId) {
        blogLikeService.likeOrUnlike(blogId, userId);
    }
}
