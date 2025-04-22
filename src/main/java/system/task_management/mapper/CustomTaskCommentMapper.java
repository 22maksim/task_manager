package system.task_management.mapper;

import system.task_management.model.TaskComment;
import system.task_management.model.dto.CommentResponseDto;

public class CustomTaskCommentMapper {

    public static CommentResponseDto toDto(TaskComment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .comment(comment.getText())
                .owner(comment.getAuthor().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
