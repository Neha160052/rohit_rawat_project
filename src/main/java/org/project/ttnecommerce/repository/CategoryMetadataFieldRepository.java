package org.project.ttnecommerce.repository;
import org.project.ttnecommerce.entity.CategoryMetadataField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CategoryMetadataFieldRepository extends JpaRepository<CategoryMetadataField, UUID> {

    Optional<CategoryMetadataField> findByNameIgnoreCaseAndIsDeletedFalse(String name);

    Page<CategoryMetadataField> findByIsDeletedFalse(Pageable pageable);

    Page<CategoryMetadataField> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, Pageable pageable);

    Optional<CategoryMetadataField> findByIdAndIsDeletedFalse(UUID id);
}