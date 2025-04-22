package system.task_management.mapper;

import system.task_management.model.OurTask;
import system.task_management.model.dto.*;
import system.task_management.model.enums.TaskStatus;

import java.util.List;
import java.util.Objects;

public class CustomOurTaskMapper {

    public static OurTask createOurTask(OurTaskRequestDto requestDto) {
        return OurTask.builder()
                .title(requestDto.title())
                .description(requestDto.description())
                .taskPriority(requestDto.priority())
                .taskStatus(TaskStatus.PENDING)
                .build();
    }

    public static OurTaskShortDto toShortDto(OurTask ourTask) {
        return new OurTaskShortDto(ourTask.getId(), ourTask.getTitle());
    }

    public static OurTaskResponseDto toResponseDto(OurTask ourTask) {
        return OurTaskResponseDto.builder()
                .id(ourTask.getId())
                .emailAuthor(ourTask.getAuthor().getEmail())
                .title(ourTask.getTitle())
                .description(ourTask.getDescription())
                .taskStatus(ourTask.getTaskStatus())
                .taskPriority(ourTask.getTaskPriority())
                .performers(getListUserAccountShortDto(ourTask))
                .comments(getListCommentDto(ourTask))
                .createdAt(ourTask.getCreatedAt())
                .updatedAt(ourTask.getUpdatedAt())
                .build();
    }

    public static List<UserAccountShortDto> getListUserAccountShortDto(OurTask ourTask) {
        if (ourTask.getPerformers() == null || ourTask.getPerformers().isEmpty()) {
            return List.of();
        }
        return ourTask.getPerformers().stream()
                .filter(Objects::nonNull)
                .map(CustomUserAccountMapper::toShortDto)
                .toList();
    }

    public static List<CommentResponseDto> getListCommentDto(OurTask ourTask) {
        if (ourTask.getComments() == null || ourTask.getComments().isEmpty()) {
            return List.of();
        }
        return ourTask.getComments().stream()
                .filter(Objects::nonNull)
                .map(CustomTaskCommentMapper::toDto)
                .toList();
    }
}
