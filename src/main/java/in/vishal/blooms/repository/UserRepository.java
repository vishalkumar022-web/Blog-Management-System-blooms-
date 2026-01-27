package in.vishal.blooms.repository;

import in.vishal.blooms.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    User findByPhoneNumber(String phoneNumber);

}
