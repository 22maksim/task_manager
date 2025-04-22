package system.task_management.model.dto;

import system.task_management.model.enums.TaskStatus;

import java.io.Serializable;
import java.time.LocalDate;

public record TaskFilterDto(
    String email,
    TaskStatus status,
    LocalDate startDate,
    LocalDate endDate,
    String title,
    int page,
    int size
    ) implements Serializable {
}
