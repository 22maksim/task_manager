package system.task_management.security.model.dto;

import java.io.Serializable;

public record RegisterResponseDto (
        String token,
        String email
) implements Serializable {
}
