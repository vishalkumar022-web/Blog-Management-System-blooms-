package in.vishal.blooms.repository;

import in.vishal.blooms.models.BlogComment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCommentRepository extends MongoRepository<BlogComment, String> {

    // Get all comments for a specific blog
    List<BlogComment> findByBlogId(String blogId);
}