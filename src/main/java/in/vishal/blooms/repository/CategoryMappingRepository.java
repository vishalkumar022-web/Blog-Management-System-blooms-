package in.vishal.blooms.repository;

import in.vishal.blooms.models.CategoryMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMappingRepository extends MongoRepository<CategoryMapping,String> {


}
