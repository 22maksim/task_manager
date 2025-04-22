package system.task_management.model.dto;

import lombok.Builder;
import system.task_management.model.enums.TaskPriority;
import system.task_management.model.enums.TaskStatus;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Builder
public record OurTaskResponseDto(
        Long id,
        String emailAuthor,
        String title,
        String description,
        TaskStatus taskStatus,
        TaskPriority taskPriority,
        List<UserAccountShortDto> performers,
        List<CommentResponseDto> comments,
        Instant createdAt,
        Instant updatedAt
) implements Serializable {
}
