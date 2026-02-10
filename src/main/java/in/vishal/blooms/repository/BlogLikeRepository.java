package in.vishal.blooms.repository;

import in.vishal.blooms.models.BlogLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogLikeRepository extends MongoRepository<BlogLike, String> {

    // Check if user already liked a blog
    Optional<BlogLike> findByBlogIdAndUserId(String blogId, String userId);

    // Get all likes for a specific blog
    List<BlogLike> findByBlogId(String blogId);
}