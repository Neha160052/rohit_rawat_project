package org.project.ttnecommerce.repository;

import org.project.ttnecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findByEmail(String email);

    Page<User> findByCustomerIsNotNullAndIsDeletedFalse(Pageable pageable);

    Page<User> findByCustomerIsNotNullAndEmailContainingIgnoreCaseAndIsDeletedFalse(
            String email,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"seller","addresses"})
    Page<User> findBySellerIsNotNullAndIsDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"seller","addresses"})
    Page<User> findBySellerIsNotNullAndEmailContainingIgnoreCaseAndIsDeletedFalse(
            String email,
            Pageable pageable
    );
}