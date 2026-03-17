package org.project.ttnecommerce.config;
import org.project.ttnecommerce.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<UUID> {
    private static final UUID SYSTEM_UUID =
            UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of(SYSTEM_UUID);
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails.getUser().getId());
        }
        return Optional.of(SYSTEM_UUID);
    }
}