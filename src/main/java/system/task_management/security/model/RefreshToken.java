package system.task_management.security.model;

import jakarta.persistence.*;
import lombok.*;
import system.task_management.model.UserAccount;

import java.time.Instant;

@Builder
@Getter
@Setter
@Entity
@Table(name = "refresh_token")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

}
