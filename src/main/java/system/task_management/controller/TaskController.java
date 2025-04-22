package system.task_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import system.task_management.model.PageResponse;
import system.task_management.model.dto.*;
import system.task_management.model.enums.TaskPriority;
import system.task_management.model.enums.TaskStatus;
import system.task_management.service.task.OurTaskService;
import system.task_management.util.UtilStandard;

import java.util.List;

@Tag(name = "Tasks", description = "Операции с задачами")
@RestController
@RequestMapping("api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final OurTaskService ourTaskServiceImpl;

    @Operation(summary = "Создать новую задачу", description = "Позволяет администратору создать задачу", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public OurTaskResponseDto createTask(@RequestBody @NotNull @Valid OurTaskRequestDto requestDto) {
        return ourTaskServiceImpl.createTask(requestDto);
    }

    @Operation(summary = "Обновить задачу",
            description = "Позволяет администратору обновить существующую задачу", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача обновлена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public OurTaskResponseDto updateTask(@RequestBody @NotNull @Valid OurTaskRequestDto requestDto) {
        return ourTaskServiceImpl.updateTask(requestDto);
    }

    @Operation(summary = "Получить задачи по фильтрам",
            description = "Позволяет получить задачи по фильтрам (например, дата, статус, исполнитель)", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Фильтр выполнен успешно")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filters")
    public PageResponse<OurTaskResponseDto> getTasksByFilters(@RequestBody TaskFilterDto taskFilterDto) {
        return PageResponse.of(ourTaskServiceImpl.getTasksByFilters(taskFilterDto));
    }

    @Operation(summary = "Получить задачу по ID",
            description = "Доступен для пользователей с правом чтения задач", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping("/{id}")
    public OurTaskResponseDto getTaskById(@PathVariable @NotNull @Min(1) Long id) {
        return ourTaskServiceImpl.getTaskById(id);
    }

    @Operation(summary = "Назначить исполнителя на задачу",
            description = "Доступен только для администратора", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Исполнитель назначен")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/assign")
    public OurTaskResponseDto assignPerformerToTask(@PathVariable("id") Long taskId, @RequestParam("userId") Long userId) {
        return ourTaskServiceImpl.assignPerformerToTask(taskId, userId);
    }

    @Operation(summary = "Удалить исполнителя из задачи", description = "Только для администратора", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Исполнитель удален")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/remove/performer")
    public OurTaskResponseDto removePerformerFromTask(@PathVariable("id") Long taskId, @RequestParam("userId") Long userId) {
        return ourTaskServiceImpl.removePerformerFromTask(taskId, userId);
    }

    @Operation(summary = "Добавить комментарий к задаче",
            description = "Доступен пользователям с правом комментирования", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий добавлен")
    })
    @PreAuthorize("hasAuthority('permission:comment')")
    @PutMapping("/{id}/comments/add")
    public OurTaskResponseDto addComment(
            @PathVariable("id") @NotNull @Min(1) Long taskId,
            @RequestParam("comment") @NotNull String comment
    ) {
        String email = UtilStandard.getCurrentUserEmail();
        return ourTaskServiceImpl.addComment(email, taskId, comment);
    }

    @Operation(summary = "Получить комментарии к задаче",
            description = "С пагинацией. Нужно делать отдельный запрос на фронте", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарии получены")
    })
    @PreAuthorize("hasAuthority('permission:comment')")
    @GetMapping("/{id}/comments")
    public PageResponse<CommentResponseDto> getTaskComments(
            @PathVariable("id") @NotNull @Min(1) Long taskId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        return PageResponse.of(ourTaskServiceImpl.getTaskComments(taskId, page, size));
    }

    @Operation(summary = "Удалить комментарий",
            description = "Удаляет комментарий по ID. Только для комментатора или администратора", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий удален"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к удалению комментария")
    })
    @PreAuthorize("hasAuthority('permission:comment')")
    @DeleteMapping("{id}/comments/remove")
    public OurTaskResponseDto removeComment(
            @PathVariable("id") @NotNull @Min(1) Long taskId,
            @RequestParam("commentId") Long commentId
    ) {
        String email = UtilStandard.getCurrentUserEmail();
        return ourTaskServiceImpl.removeComment(email, taskId, commentId);
    }

    @Operation(summary = "Получить исполнителей задачи", description = "Доступно только администратору", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список исполнителей получен")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/performers")
    public List<UserAccountShortDto> getPerformersFromTaskId(@PathVariable("id") @NotNull @Min(1) Long id) {
        return ourTaskServiceImpl.getPerformersFromTaskId(id);
    }

    @Operation(summary = "Изменить статус задачи", description = "Только для администратора", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус задачи изменен")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public OurTaskResponseDto setStatus(
            @PathVariable("id") @NotNull @Min(1) Long taskId,
            @RequestParam("status") @NotNull TaskStatus status
    ) {
        return ourTaskServiceImpl.setStatus(taskId, status);
    }

    @Operation(summary = "Изменить приоритет задачи", description = "Только для администратора", tags = "Tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Приоритет изменен")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/priority")
    public OurTaskResponseDto setPriority(
            @PathVariable("id") @NotNull @Min(1) Long taskId,
            @RequestParam("priority") @NotNull TaskPriority priority) {
        return ourTaskServiceImpl.setPriority(taskId, priority);
    }
}
