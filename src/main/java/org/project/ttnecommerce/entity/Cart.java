package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.project.ttnecommerce.entity.base.Auditable;

import java.io.Serializable;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = {"customer_user_id","product_variation_id"}))
public class Cart extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_user_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "product_variation_id")
    private ProductVariation productVariation;

    private Integer quantity;

    private Boolean isWishlistItem = false;
}