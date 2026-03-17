/*
package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "category_metadata_field_values",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"category_id","category_metadata_field_id"})
        }
)
public class CategoryMetadataFieldValues {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String values;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_metadata_field_id")
    private CategoryMetadataField field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}*/
