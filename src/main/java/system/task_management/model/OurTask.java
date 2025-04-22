package system.task_management.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import system.task_management.model.enums.TaskPriority;
import system.task_management.model.enums.TaskStatus;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "our_task")
@NoArgsConstructor
@AllArgsConstructor
public class OurTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "ourTask", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<TaskComment> comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus taskStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "priotity", nullable = false)
    private TaskPriority taskPriority;

    @ManyToMany(mappedBy = "ourTasks", fetch = FetchType.EAGER)
    private List<UserAccount> performers;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserAccount author;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

}
