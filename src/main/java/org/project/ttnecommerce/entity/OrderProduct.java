package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.project.ttnecommerce.entity.base.Auditable;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProduct extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_variation_id")
    private ProductVariation productVariation;

    private Integer quantity;

    private Double price;
}