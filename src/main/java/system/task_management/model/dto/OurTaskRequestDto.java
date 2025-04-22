package system.task_management.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import system.task_management.model.enums.TaskPriority;

import java.io.Serializable;

@Builder
public record OurTaskRequestDto(
        @Email String authorEmail,
        @NotBlank @Size(min = 3, max = 255) String title,
        @NotBlank @Size(min = 3, max = 4500) String description,
        @NotNull TaskPriority priority
) implements Serializable {
}
