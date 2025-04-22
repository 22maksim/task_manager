package system.task_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import system.task_management.model.UserAccount;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);

    boolean existsByEmail(String email);

}
