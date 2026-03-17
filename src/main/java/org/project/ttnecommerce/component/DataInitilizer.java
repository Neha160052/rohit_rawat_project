package org.project.ttnecommerce.component;

import jakarta.annotation.PostConstruct;
import org.project.ttnecommerce.entity.Role;
import org.project.ttnecommerce.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitilizer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {

        if (roleRepository.findByAuthority("CUSTOMER").isEmpty()) {
            Role customer = new Role();
            customer.setAuthority("CUSTOMER");
            roleRepository.save(customer);
        }

        if (roleRepository.findByAuthority("SELLER").isEmpty()) {
            Role seller = new Role();
            seller.setAuthority("SELLER");
            roleRepository.save(seller);
        }
    }
}