//
//package in.vishal.blooms.repository;
//
//import in.vishal.blooms.models.User;
//import in.vishal.blooms.specifications.UserSpec;
//import jakarta.persistence.EntityManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class UserCustomRepository {
//    private EntityManager entityManager;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public UserCustomRepository(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }
//
//    public User saveUser(User user){
//        entityManager.persist(user);
//        return user;
//    }
//
//    public List<User> findUsersByEmail(String email){
//        //sql
////        String query = "SELECT u FROM User u WHERE u.email = :email";
////        return entityManager.createQuery(query, User.class)
////                .setParameter("email", email)
////                .getResultList();
//        return userRepository.findAll(UserSpec.hasEmail(email));
//    }
//
//    public List<User> findUsersWithAgeAbove(int age){
//        String query = "SELECT u FROM User u WHERE u.age > :age";
//        return entityManager.createQuery(query, User.class)
//                .setParameter("age", age)
//                .getResultList();
//    }
//
//    public List<User> findUsersWithAgeAboveUsingCB(int age){
////        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
////        CriteriaQuery<User> cq = cb.createQuery(User.class);
////
////        Root<User> user = cq.from(User.class);
////
////        List<Predicate> predicate = new ArrayList<>();
////        predicate.add(cb.greaterThan(user.get("age"), age));
////        predicate.add(cb.equal(user.get("isAdmin"), true));
////
////        cq.where(cb.and(predicate.toArray(new Predicate[0])));
////
////        return entityManager.createQuery(cq).getResultList();
//
//        return userRepository.findAll(UserSpec.hasAgeGreaterThan(age).and(UserSpec.isAdmin("true")));
//    }
//
//    // criteria builder
//
//
//
//
//}
