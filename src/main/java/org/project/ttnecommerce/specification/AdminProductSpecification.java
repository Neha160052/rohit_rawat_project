package org.project.ttnecommerce.specification;

import org.project.ttnecommerce.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class AdminProductSpecification {

    public static Specification<Product> filterProducts(UUID sellerId, UUID categoryId) {

        return (root, query, cb) -> {

            var predicates = cb.conjunction();

            predicates = cb.and(predicates, cb.isFalse(root.get("isDeleted")));

            if (sellerId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("seller").get("id"), sellerId));
            }

            if (categoryId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("category").get("id"), categoryId));
            }

            return predicates;
        };
    }
}