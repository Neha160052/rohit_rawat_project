package org.project.ttnecommerce.entity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.project.ttnecommerce.entity.base.Auditable;

import java.util.UUID;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer extends Auditable {

    @Id
    private UUID id;

    private String contact;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
}