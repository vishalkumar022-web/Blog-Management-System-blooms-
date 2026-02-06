package in.vishal.blooms.repository;

import in.vishal.blooms.models.BlogComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogCommentRepository extends MongoRepository<BlogComment, String> {

    List<BlogComment> findByBlogId(String blogId);


}
