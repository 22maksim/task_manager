package system.task_management.model.dto;

import lombok.*;
import system.task_management.security.model.RoleUser;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountResponseDto implements Serializable {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private List<OurTaskShortDto> ourTasks;
    private Instant createdAt;
    private Instant updatedAt;
}
