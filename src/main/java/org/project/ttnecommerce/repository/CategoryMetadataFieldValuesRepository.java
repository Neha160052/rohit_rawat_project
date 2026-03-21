package org.project.ttnecommerce.repository;
import org.project.ttnecommerce.entity.Category;
import org.project.ttnecommerce.entity.CategoryMetadataField;
import org.project.ttnecommerce.entity.CategoryMetadataFieldValues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryMetadataFieldValuesRepository extends JpaRepository<CategoryMetadataFieldValues, UUID> {

    boolean existsByCategoryAndMetadataField(Category category, CategoryMetadataField metadataField);
    List<CategoryMetadataFieldValues> findByCategory(Category category);

}