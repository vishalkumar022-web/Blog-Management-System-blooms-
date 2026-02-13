package in.vishal.blooms.repository;

import in.vishal.blooms.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    // ✅ FIX: Added 'IgnoreCase' for Status
    Page<Category> findByNameContainingIgnoreCaseAndStatusIgnoreCaseAndActive(
            String name,
            String status,
            boolean active,
            Pageable pageable
    );

    // ✅ Get All Active Categories (Paginated)
    Page<Category> findByActiveTrue(Pageable pageable);
}