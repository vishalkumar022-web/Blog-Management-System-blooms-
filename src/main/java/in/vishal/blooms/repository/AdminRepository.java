package in.vishal.blooms.repository;

import in.vishal.blooms.models.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {

    Admin findByPhoneNumber(String phoneNumber);
}
