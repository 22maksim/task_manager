package system.task_management.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import system.task_management.model.UserAccount;
import system.task_management.security.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    Optional<RefreshToken> findByEmail(String email);
}
