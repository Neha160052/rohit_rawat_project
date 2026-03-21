package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.project.ttnecommerce.Enum.OrderStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_id")
    private OrderProduct orderProduct;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum fromStatus;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum toStatus;

    private String transitionNotes;

    private LocalDateTime transitionDate;
}