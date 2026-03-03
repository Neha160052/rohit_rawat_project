package org.project.ttnecommerce.repository;
import org.project.ttnecommerce.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    public Optional<Role> findByAuthority(String name);
}
