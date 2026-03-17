/*
package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class Cart {
    @EmbeddedId
    private CartId id;
    private Integer quantity;
    private boolean isWishlistItem = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerUserId")
    @JoinColumn(name = "customer_user_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productVariationId")
    @JoinColumn(name = "product_variation_id")
    private ProductVariation productVariation;
}
*/
