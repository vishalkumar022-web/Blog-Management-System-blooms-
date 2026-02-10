//
//package in.vishal.blooms.specifications;
//
//import in.vishal.blooms.models.User;
//import org.springframework.data.jpa.domain.PredicateSpecification;
//import org.springframework.data.jpa.domain.Specification;
//
//public class UserSpec {
//    public static Specification<User> hasEmail(String email) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email);
//    }
//
//    public static Specification<User> hasAgeGreaterThan(int age) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("age"), age);
//    }
//
//    public static Specification<User> isAdmin(String isAdmin) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isAdmin"), isAdmin);
//    }
//}
