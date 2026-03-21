package org.project.ttnecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.project.ttnecommerce.entity.base.Auditable;

import java.util.UUID;

@Entity
@Getter
@Setter
public class ProductVariationImage extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variation_id")
    private ProductVariation productVariation;

    private String imageName;

    private Boolean isPrimary = false;
}