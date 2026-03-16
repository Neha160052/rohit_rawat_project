package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.project.ttnecommerce.entity.base.Auditable;
import java.util.UUID;


@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"seller_user_id","name","brand","category_id"}
        ))
@Getter
@Setter
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_user_id")
    private User seller;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Boolean isCancellable = false;
    private Boolean isReturnable = false;

    private String brand;

    private Boolean isActive = false;
    private Boolean isDeleted = false;
}