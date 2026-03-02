package org.project.ttnecommerce.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="user")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String firstName;

    @Size(max = 50)
    private String middleName;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String lastName;
    @NotBlank
    @Size(min = 8)
    @Column(nullable = false)
    private String password;

    @NotNull
    private Boolean isDeleted = false;

    @NotNull
    private Boolean isActive = true;

    @NotNull
    private Boolean isExpired = false;

    @NotNull
    private Boolean isLocked = false;

    @Min(0)
    private Integer invalidAttemptCount = 0;

    private LocalDateTime passwordUpdateDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();
}
