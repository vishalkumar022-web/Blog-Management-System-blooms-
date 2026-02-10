package in.vishal.blooms.service;

import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.Blog;
import in.vishal.blooms.models.BlogComment;
import in.vishal.blooms.models.Status;
import in.vishal.blooms.repository.BlogCommentRepository;
import in.vishal.blooms.repository.BlogRepository;
import in.vishal.blooms.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BlogCommentService {

    private static final Logger log = LoggerFactory.getLogger(BlogCommentService.class);

    private final BlogCommentRepository blogCommentRepository;
    private final BlogRepository blogRepository;

    public BlogCommentService(BlogCommentRepository blogCommentRepository, BlogRepository blogRepository) {
        this.blogCommentRepository = blogCommentRepository;
        this.blogRepository = blogRepository;
    }

    public ApiResponse<String> addComment(String blogId, String userId, String text) {
        log.info("Adding comment on BlogID: {} by UserID: {}", blogId, userId);

        Optional<Blog> blogOpt = blogRepository.findById(blogId);

        if (blogOpt.isEmpty()) {
            log.error("Blog not found for ID: {}", blogId);
            throw new ApplicationException("Blog not found for ID: " + blogId);
        }

        String currentStatus = blogOpt.get().getStatus();
        String requiredStatus = Status.PUBLISHED.getDisplayName();

        if (!currentStatus.equalsIgnoreCase(requiredStatus)) {
            log.error("Blog status mismatch. DB has: {}, Required: {}", currentStatus, requiredStatus);
            throw new ApplicationException("Blog is not published yet, cannot comment.");
        }

        try {
            BlogComment comment = new BlogComment();
            comment.setId(String.valueOf(System.currentTimeMillis()));
            comment.setBlogId(blogId);
            comment.setUserId(userId);
            comment.setCommentText(text);
            comment.setCreatedDTTM(LocalDateTime.now());

            blogCommentRepository.save(comment);

            return new ApiResponse<>(true, "Comment added successfully", null);

        } catch (Exception e) {
            log.error("Error adding comment: {}", e.getMessage());
            throw new ApplicationException("Failed to add comment");
        }
    }
}