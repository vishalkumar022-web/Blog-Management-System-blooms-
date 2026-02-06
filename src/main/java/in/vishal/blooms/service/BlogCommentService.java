package in.vishal.blooms.service;

import in.vishal.blooms.models.Blog;
import in.vishal.blooms.models.BlogComment;
import in.vishal.blooms.models.Status;
import in.vishal.blooms.repository.BlogCommentRepository;
import in.vishal.blooms.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BlogCommentService {

    private final BlogCommentRepository blogCommentRepository;
    private final BlogRepository blogRepository;

    public BlogCommentService(BlogCommentRepository blogCommentRepository,
                              BlogRepository blogRepository) {
        this.blogCommentRepository = blogCommentRepository;
        this.blogRepository = blogRepository;
    }

    public void addComment(String blogId, String userId, String text) {

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



        BlogComment comment = new BlogComment();
        comment.setId(String.valueOf(System.currentTimeMillis()));
        comment.setBlogId(blogId);
        comment.setUserId(userId);
        comment.setCommentText(text);
        comment.setCreatedDTTM(LocalDateTime.now());

        blogCommentRepository.save(comment);
    }


}
