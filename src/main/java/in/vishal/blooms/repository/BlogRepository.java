package in.vishal.blooms.repository;

import in.vishal.blooms.models.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends MongoRepository<Blog, String> {


    List<Blog> findByTitleContainingIgnoreCaseAndStatusAndActive(
            String title,
            String status,
            boolean active
    );



}
