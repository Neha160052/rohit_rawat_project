package org.project.ttnecommerce.repository;
import org.project.ttnecommerce.entity.ActivationToken;
import org.project.ttnecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, UUID> {

    Optional<ActivationToken> findByToken(String token);

    Optional<ActivationToken> findByUser(User user);

    void deleteByUser(User user);
}
