package org.project.ttnecommerce.repository;

import org.project.ttnecommerce.entity.ProductVariationImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductVariationImageRepository extends JpaRepository<ProductVariationImage, UUID> {
}