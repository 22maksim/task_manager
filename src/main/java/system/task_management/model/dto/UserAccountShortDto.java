package system.task_management.model.dto;

import java.io.Serializable;

public record UserAccountShortDto(
        Long id,
        String email,
        String firstname,
        String lastname
) implements Serializable {
}
