package in.vishal.blooms.repository;

import in.vishal.blooms.models.BlogLike;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BlogLikeRepository extends MongoRepository<BlogLike, String> {

    Optional<BlogLike> findByBlogIdAndUserId(String blogId, String userId);

    List<BlogLike> findByBlogId(String blogId);


}
