package in.vishal.blooms.repository;

import in.vishal.blooms.models.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // ✅ Correct Import
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends MongoRepository<Blog, String> {

    // ✅ Search with Pagination
    Page<Blog> findByTitleContainingIgnoreCaseAndStatusAndActive(String title, String status, boolean active, Pageable pageable);


}