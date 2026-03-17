package org.project.ttnecommerce.repository;
import org.project.ttnecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}