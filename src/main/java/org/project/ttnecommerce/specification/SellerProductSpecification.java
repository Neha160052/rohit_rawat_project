package org.project.ttnecommerce.specification;
import jakarta.persistence.criteria.Predicate;
import org.project.ttnecommerce.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SellerProductSpecification {
    public static Specification<Product> filterProducts(UUID sellerId, UUID productId, UUID categoryId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("seller").get("id"), sellerId));
            predicates.add(cb.isFalse(root.get("isDeleted")));
            if (productId != null) {
                predicates.add(cb.equal(root.get("id"), productId));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}