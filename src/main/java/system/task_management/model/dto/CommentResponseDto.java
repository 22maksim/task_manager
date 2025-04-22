package system.task_management.model.dto;

import lombok.Builder;

import java.io.Serializable;
import java.time.Instant;

@Builder
public record CommentResponseDto(
        Long id,
        String comment,
        Long owner,
        Instant createdAt
) implements Serializable {
}
