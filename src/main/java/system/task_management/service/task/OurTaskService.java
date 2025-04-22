package system.task_management.service.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import system.task_management.model.dto.*;
import system.task_management.model.enums.TaskPriority;
import system.task_management.model.enums.TaskStatus;

import java.util.List;

public interface OurTaskService {

    OurTaskResponseDto createTask(OurTaskRequestDto requestDto);

    Page<CommentResponseDto> getTaskComments(Long taskId, int page, int size);

    Page<OurTaskResponseDto> getTasksByFilters(TaskFilterDto taskFilterDto);

    OurTaskResponseDto getTaskById(Long id);

    OurTaskResponseDto assignPerformerToTask(Long taskId, Long userId);

    OurTaskResponseDto removePerformerFromTask(Long taskId, Long userId);

    List<UserAccountShortDto> getPerformersFromTaskId(Long id);

    OurTaskResponseDto setStatus(Long taskId, TaskStatus status);

    OurTaskResponseDto setPriority(Long taskId, TaskPriority priority);

    OurTaskResponseDto updateTask(OurTaskRequestDto requestDto);

    OurTaskResponseDto addComment(String email, Long taskId, String comment);

    OurTaskResponseDto removeComment(String email, Long taskId, Long commentId);
}
