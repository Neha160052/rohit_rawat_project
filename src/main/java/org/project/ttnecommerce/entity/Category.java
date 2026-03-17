/*
package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Category> subCategories = new HashSet<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<CategoryMetadataFieldValues> metadataValues = new HashSet<>();
}*/
