package in.vishal.blooms.service;

import in.vishal.blooms.models.Blog;
import in.vishal.blooms.models.BlogLike;
import in.vishal.blooms.models.Status;
import in.vishal.blooms.repository.BlogLikeRepository;
import in.vishal.blooms.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BlogLikeService {

    private final BlogLikeRepository blogLikeRepository;
    private final BlogRepository blogRepository;

    public BlogLikeService(BlogLikeRepository blogLikeRepository,
                           BlogRepository blogRepository) {
        this.blogLikeRepository = blogLikeRepository;
        this.blogRepository = blogRepository;
    }

    // LIKE / UNLIKE TOGGLE
    public void likeOrUnlike(String blogId, String userId) {
        System.out.println("DEBUG: likeOrUnlike called for BlogID: " + blogId + ", UserID: " + userId); // DEBUG LOG 1

        Optional<Blog> blogOpt = blogRepository.findById(blogId);

        if (blogOpt.isEmpty()) {
            System.out.println("ERROR: Blog not found for ID: " + blogId); // Pata chalega agar ID galat hai
            return; // Ya exception throw karo
        }

        String currentStatus = blogOpt.get().getStatus();
        String requiredStatus = Status.PUBLISHED.getDisplayName();

        if (!currentStatus.equalsIgnoreCase(requiredStatus)) {
            System.out.println("ERROR: Blog status mismatch. DB has: " + currentStatus + ", Required: " + requiredStatus);
            return;
        }




        Optional<BlogLike> existingLike = blogLikeRepository.findByBlogIdAndUserId(blogId, userId);

        if (existingLike.isPresent()) {
            // UNLIKE
            blogLikeRepository.delete(existingLike.get());
        } else {
            // LIKE
            BlogLike like = new BlogLike();
            like.setId(String.valueOf(System.currentTimeMillis()));
            like.setBlogId(blogId);
            like.setUserId(userId);
            like.setCreatedDTTM(LocalDateTime.now());

            blogLikeRepository.save(like);
        }
    }
}
