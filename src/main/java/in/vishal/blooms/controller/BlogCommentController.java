package in.vishal.blooms.controller;

import in.vishal.blooms.service.BlogCommentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blog/comment")
public class BlogCommentController {

    private final BlogCommentService blogCommentService;

    public BlogCommentController(BlogCommentService blogCommentService) {
        this.blogCommentService = blogCommentService;
    }

    @PostMapping
    public void addComment(@RequestParam String blogId,
                           @RequestParam String userId,
                           @RequestParam String text) {
        blogCommentService.addComment(blogId, userId, text);
    }
}
