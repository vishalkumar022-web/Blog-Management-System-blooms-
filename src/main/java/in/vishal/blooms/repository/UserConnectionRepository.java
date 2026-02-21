package in.vishal.blooms.repository;

import in.vishal.blooms.models.UserConnection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// @Repository Spring Boot ko batata hai ki ye Database ka "Naukarr/Manager" hai.
// MongoRepository humein saare database operations (save, delete, find) free me de deta hai bina SQL likhe!
@Repository
public interface UserConnectionRepository extends MongoRepository<UserConnection, String> {

    // 1. CHECK KARNE KE LIYE: Kya A ne B ko pehle se follow kiya hai?
    // Ye method database me un dono IDs ko ek sath dhundhta hai.
    Optional<UserConnection> findByFollowerIdAndFollowingId(String followerId, String followingId);

    // 2. MERI FOLLOWING LIST: Mai kis-kis ko follow kar raha hu?
    // Jahan-jahan followerId me "Mera ID" hai, wo sab le aao.
    List<UserConnection> findByFollowerId(String followerId);

    // 3. MERE FOLLOWERS LIST: Kis-kis ne mujhe follow kiya hai?
    // Jahan-jahan followingId me "Mera ID" hai, wo sab log le aao.
    List<UserConnection> findByFollowingId(String followingId);

    // 4. UNFOLLOW KARNA:
    // Jis table entry me mera aur samne wale ka ID match ho, us entry ko delete kar do (Unfollow).
    void deleteByFollowerIdAndFollowingId(String followerId, String followingId);



    // NAYE PRO METHODS: Sirf Ginti (Count) laane ke liye!
    // ==========================================
    // Ye method poori list nahi layega, sirf ek Number (long) layega, isse RAM bachegi.
    long countByFollowerId(String followerId);
    long countByFollowingId(String followingId);

}