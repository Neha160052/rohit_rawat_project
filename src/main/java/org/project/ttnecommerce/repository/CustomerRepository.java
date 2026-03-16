package org.project.ttnecommerce.repository;

import org.project.ttnecommerce.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByContact(String newContact);
}