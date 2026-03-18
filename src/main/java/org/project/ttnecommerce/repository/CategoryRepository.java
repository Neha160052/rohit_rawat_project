package org.project.ttnecommerce.repository;
import org.project.ttnecommerce.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByNameIgnoreCaseAndParentCategoryIsNullAndIsDeletedFalse(String name);

    Optional<Category> findByNameIgnoreCaseAndParentCategoryIdAndIsDeletedFalse(String name, UUID parentId);

    Page<Category> findByIsDeletedFalse(Pageable pageable);
    Page<Category> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, Pageable pageable);
    Optional<Category> findByIdAndIsDeletedFalse(UUID id);

    List<Category> findByIsDeletedFalse();

    List<Category> findByParentCategoryIsNullAndIsDeletedFalse();

    List<Category> findByParentCategoryAndIsDeletedFalse(Category parent);

    boolean existsByParentCategoryAndIsDeletedFalse(Category category);


}