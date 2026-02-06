package in.vishal.blooms.repository;

import in.vishal.blooms.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    User findByPhoneNumber(String phoneNumber);

    // ✅ FIXED: 'Active' -> 'IsActive' (Kyunki model me field "isActive" hai)
    List<User> findByNameContainingIgnoreCaseAndIsActive(String name, boolean active);
}