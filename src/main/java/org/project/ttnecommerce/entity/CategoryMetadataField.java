package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import org.project.ttnecommerce.entity.base.Auditable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category_metadata_field")
public class CategoryMetadataField extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private Boolean isDeleted = false;
}
