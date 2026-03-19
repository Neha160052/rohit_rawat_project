package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.project.ttnecommerce.entity.base.Auditable;

import java.util.List;
import java.util.UUID;
@Entity
@Getter
@Setter
public class ProductVariation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "productVariation")
    private List<ProductVariationImage> images;

    private Integer quantityAvailable;
    private Double price;

    @Column(columnDefinition = "json")
    private String metadata;

    private String primaryImageName;
    private Boolean isActive = true;
    private Boolean isDeleted = false;
}