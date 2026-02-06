package in.vishal.blooms.repository;

import in.vishal.blooms.models.SubCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends MongoRepository<SubCategory,String> {

    // ✅ FIXED: 'Status' ke baad 'IgnoreCase' lagaya taaki "published" aur "Published" dono chalein
    List<SubCategory> findByNameContainingIgnoreCaseAndStatusIgnoreCaseAndActive(
            String name,
            String status,
            boolean active
    );

    // 2. Get by Category ID
    List<SubCategory> findByCategoryId(String categoryId);
}