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
@Table(name = "category_metadata_field")
public class CategoryMetadataField {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private Set<CategoryMetadataFieldValues> values = new HashSet<>();
}*/
