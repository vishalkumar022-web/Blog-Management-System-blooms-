package in.vishal.blooms.repository;

import in.vishal.blooms.models.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends MongoRepository<Blog, String> {

    // ✅ FIX: Added 'IgnoreCase' for Status to solve "published" vs "Published" issue
    Page<Blog> findByTitleContainingIgnoreCaseAndStatusIgnoreCaseAndActive(String title, String status, boolean active, Pageable pageable);

    // Interface ke andar ye line add karo:
    List<Blog> findByAuthorId(String authorId);

}