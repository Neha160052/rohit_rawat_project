package org.project.ttnecommerce.repository;
import org.project.ttnecommerce.entity.Category;
import org.project.ttnecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    @Query("""
        SELECT DISTINCT p.brand
        FROM Product p
        WHERE p.category IN :categories
        AND p.isDeleted = false
        AND p.isActive = true
    """)
    List<String> findDistinctBrands(List<Category> categories);

    boolean existsByCategoryAndIsDeletedFalse(Category category);

    boolean existsBySellerIdAndNameAndBrandAndCategoryIdAndIsDeletedFalse(
            UUID sellerId,
            String name,
            String brand,
            UUID categoryId
    );

    @Override
    @EntityGraph(attributePaths = {"category", "variations"})
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"seller"})
    Optional<Product> findByIdAndIsDeletedFalse(UUID id);


    @EntityGraph(attributePaths = {"seller"})
    Optional<Product> findByIdAndSellerIdAndIsDeletedFalse(UUID productId, UUID sellerId);
}