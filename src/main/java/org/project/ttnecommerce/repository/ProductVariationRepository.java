package org.project.ttnecommerce.repository;

import org.project.ttnecommerce.entity.Category;
import org.project.ttnecommerce.entity.Product;
import org.project.ttnecommerce.entity.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, UUID> {


    @Query("""
SELECT MIN(pv.price), MAX(pv.price)
FROM ProductVariation pv
JOIN pv.product p
WHERE p.category.id IN :categoryIds
AND p.isDeleted = false
AND p.isActive = true
""")
    List<Object[]> findMinMaxPrice(@Param("categoryIds") List<UUID> categoryIds);



    List<ProductVariation> findByProductAndIsDeletedFalse(Product product);
}