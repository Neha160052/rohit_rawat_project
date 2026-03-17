/*
package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_variation")
public class ProductVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer quantityAvailable;
    private BigDecimal price;
    private String primaryImageName;

    @Column(columnDefinition = "json")
    private String metadata;

    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "productVariation")
    private Set<Cart> carts = new HashSet<>();
}*/
