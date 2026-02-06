package in.vishal.blooms.repository;

import in.vishal.blooms.models.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category,String> {

    // New Search Method
    List<Category> findByNameContainingIgnoreCaseAndStatusAndActive(
            String name,
            String status,
            boolean active
    );
}