package in.vishal.blooms.repository;

import in.vishal.blooms.models.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // ✅ Correct Import
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends MongoRepository<SubCategory, String> {

    // ✅ Search SubCategories (Paginated)
    Page<SubCategory> findByNameContainingIgnoreCaseAndStatusIgnoreCaseAndActive(
            String name,
            String status,
            boolean active,
            Pageable pageable
    );

    // ✅ Get All Active SubCategories (Paginated)
    Page<SubCategory> findByActiveTrue(Pageable pageable);

    // ✅ Get SubCategories by CategoryID (List hi return karega kyunki ye dropdown ke liye hota hai)
    List<SubCategory> findByCategoryId(String categoryId);
}