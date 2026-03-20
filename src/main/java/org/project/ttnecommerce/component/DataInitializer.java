package org.project.ttnecommerce.component;
import jakarta.annotation.PostConstruct;
import org.project.ttnecommerce.entity.Role;
import org.project.ttnecommerce.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        createRole("ROLE_ADMIN");
        createRole("ROLE_CUSTOMER");
        createRole("ROLE_SELLER");
    }

    private void createRole(String roleName) {
        if (roleRepository.findByAuthority(roleName).isEmpty()) {
            Role role = new Role();
            role.setAuthority(roleName);
            roleRepository.save(role);
        }
    }
}