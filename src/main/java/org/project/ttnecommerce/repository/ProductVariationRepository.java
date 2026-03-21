package org.project.ttnecommerce.repository;

import org.project.ttnecommerce.entity.Product;
import org.project.ttnecommerce.entity.ProductVariation;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
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

    @EntityGraph(attributePaths = {"images"})
    List<ProductVariation> findByProductAndIsDeletedFalse(Product product, Sort sort);


    @EntityGraph(attributePaths = {"images"})
    Optional<ProductVariation> findByIdAndProduct(UUID id, Product product);

    @EntityGraph(attributePaths = {"images"})
    @Query("""
        SELECT v FROM ProductVariation v
        WHERE v.product = :product
        AND v.isDeleted = false
        AND LOWER(v.metadata) LIKE %:query%
    """)
    Page<ProductVariation> searchByProduct(
            @Param("product") Product product,
            @Param("query") String query,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"images"})
    @Query("""
        SELECT v FROM ProductVariation v
        WHERE v.product = :product
        AND v.isDeleted = false
        AND LOWER(v.metadata) LIKE %:query%
    """)
    List<ProductVariation> searchByProductWithoutPaging(
            @Param("product") Product product,
            @Param("query") String query,
            Sort sort
    );

    @Query("""
        SELECT v FROM ProductVariation v
        WHERE v.product = :product
        AND v.metadata = :metadata
        AND v.isDeleted = false
    """)
    Optional<ProductVariation> findDuplicateVariation(
            @Param("product") Product product,
            @Param("metadata") String metadata
    );

    @EntityGraph(attributePaths = {"images"})
    @Query("""
        SELECT v FROM ProductVariation v
        WHERE v.product = :product
        AND v.isDeleted = false
        AND v.isActive = true
        AND v.quantityAvailable > 0
    """)
    List<ProductVariation> findValidVariations(@Param("product") Product product);
}