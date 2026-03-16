package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.project.ttnecommerce.entity.base.Auditable;

import java.util.*;
@Entity
@Table(name = "category")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<CategoryMetadataFieldValues> categoryMetadataFieldValues = new ArrayList<>();

    private Boolean isDeleted = false;

}