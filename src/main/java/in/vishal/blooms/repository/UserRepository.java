package in.vishal.blooms.repository;

import in.vishal.blooms.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // ✅ Correct Import
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    // ✅ Pagination Search (Return type Page)
    Page<User> findByNameContainingIgnoreCaseAndIsActive(String name, boolean active, Pageable pageable);
}