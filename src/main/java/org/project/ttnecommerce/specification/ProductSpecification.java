package org.project.ttnecommerce.specification;
import jakarta.persistence.criteria.*;
import org.project.ttnecommerce.dto.ProductFilterRequest;
import org.project.ttnecommerce.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filterProducts(ProductFilterRequest request) {

        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("isDeleted")));
            predicates.add(cb.isTrue(root.get("isActive")));

            if (request.getSellerId() != null) {
                predicates.add(cb.equal(root.get("seller").get("id"), request.getSellerId()));
            }

            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), request.getCategoryId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}