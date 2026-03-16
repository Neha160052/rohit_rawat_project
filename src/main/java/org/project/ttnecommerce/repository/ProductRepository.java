package org.project.ttnecommerce.repository;

import org.project.ttnecommerce.entity.Category;
import org.project.ttnecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("""
        SELECT DISTINCT p.brand
        FROM Product p
        WHERE p.category IN :categories
        AND p.isDeleted = false
        AND p.isActive = true
    """)
    List<String> findDistinctBrands(List<Category> categories);

    boolean existsByCategoryAndIsDeletedFalse(Category category);
    boolean existsBySellerIdAndNameIgnoreCaseAndBrandIgnoreCaseAndCategoryIdAndIsDeletedFalse(UUID sellerId, String name, String brand, UUID categoryId);
}