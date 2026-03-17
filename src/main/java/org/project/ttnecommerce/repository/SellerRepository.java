package org.project.ttnecommerce.repository;
import org.project.ttnecommerce.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, UUID> {

    boolean existsByGst(String gst);
    boolean existsByCompanyNameIgnoreCase(String companyName);
}