package in.vishal.blooms.service;

import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.models.Blog;
import in.vishal.blooms.models.BlogLike;
import in.vishal.blooms.models.Status;
import in.vishal.blooms.repository.BlogLikeRepository;
import in.vishal.blooms.repository.BlogRepository;
import in.vishal.blooms.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BlogLikeService {

    private static final Logger log = LoggerFactory.getLogger(BlogLikeService.class);

    private final BlogLikeRepository blogLikeRepository;
    private final BlogRepository blogRepository;

    public BlogLikeService(BlogLikeRepository blogLikeRepository, BlogRepository blogRepository) {
        this.blogLikeRepository = blogLikeRepository;
        this.blogRepository = blogRepository;
    }

    public ApiResponse<String> likeOrUnlike(String blogId, String userId) {
        log.info("Toggle like called for BlogID: {}, UserID: {}", blogId, userId);

        Optional<Blog> blogOpt = blogRepository.findById(blogId);

        if (blogOpt.isEmpty()) {
            log.error("Blog not found for ID: {}", blogId);
            throw new ApplicationException("Blog not found for ID: " + blogId);
        }

        String currentStatus = blogOpt.get().getStatus();
        String requiredStatus = Status.PUBLISHED.getDisplayName();

        if (!currentStatus.equalsIgnoreCase(requiredStatus)) {
            log.error("Blog status mismatch. DB has: {}, Required: {}", currentStatus, requiredStatus);
            throw new ApplicationException("Blog is not published yet, so you cannot like it.");
        }

        try {
            Optional<BlogLike> existingLike = blogLikeRepository.findByBlogIdAndUserId(blogId, userId);

            if (existingLike.isPresent()) {
                // UNLIKE
                blogLikeRepository.delete(existingLike.get());
                return new ApiResponse<>(true, "Unliked successfully", null);
            } else {
                // LIKE
                BlogLike like = new BlogLike();
                like.setId(String.valueOf(System.currentTimeMillis()));
                like.setBlogId(blogId);
                like.setUserId(userId);
                like.setCreatedDTTM(LocalDateTime.now());

                blogLikeRepository.save(like);
                return new ApiResponse<>(true, "Liked successfully", null);
            }
        } catch (Exception e) {
            log.error("Error in like/unlike: {}", e.getMessage());
            throw new ApplicationException("Failed to process like/unlike");
        }
    }
}