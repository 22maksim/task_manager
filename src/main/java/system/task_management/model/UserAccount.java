package system.task_management.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import system.task_management.security.model.RoleUser;
import system.task_management.model.enums.UserAccountStatus;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@Table(name = "user_account")
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleUser role;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_task",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private List<OurTask> ourTasks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserAccountStatus userAccountStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", updatable = false)
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return id.equals(that.id) && email.equals(that.email);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "data: " + firstName + " " + lastName + " " + email + " " + role + " " + userAccountStatus + " " + createdAt
                + " " + updatedAt + " " + id + "}";
    }
}
