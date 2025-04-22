package system.task_management.service.task;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import system.task_management.exception.TaskNotFoundException;
import system.task_management.exception.UserEntityNotFound;
import system.task_management.mapper.CustomOurTaskMapper;
import system.task_management.mapper.CustomTaskCommentMapper;
import system.task_management.mapper.CustomUserAccountMapper;
import system.task_management.model.OurTask;
import system.task_management.model.TaskComment;
import system.task_management.model.UserAccount;
import system.task_management.model.dto.*;
import system.task_management.model.enums.TaskPriority;
import system.task_management.model.enums.TaskStatus;
import system.task_management.repository.OurTaskRepository;
import system.task_management.repository.TaskCommentRepository;
import system.task_management.repository.UserAccountRepository;
import system.task_management.security.model.RoleUser;
import system.task_management.specification.TaskSpecifications;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OurTaskServiceImpl implements OurTaskService {
    private final TaskCommentRepository taskCommentRepository;
    private final OurTaskRepository ourTaskRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    @Override
    public OurTaskResponseDto createTask(OurTaskRequestDto requestDto) {
        OurTask ourTask = CustomOurTaskMapper.createOurTask(requestDto);
        ourTask.setAuthor(userAccountRepository.findByEmail(requestDto.authorEmail())
                .orElseThrow(() -> new UserEntityNotFound("User Not Found. Email: " + requestDto.authorEmail())));
        ourTask.setTaskStatus(TaskStatus.PENDING);

        ourTask = ourTaskRepository.save(ourTask);
        return CustomOurTaskMapper.toResponseDto(ourTask);
    }

    @Transactional
    @Override
    public Page<OurTaskResponseDto> getTasksByFilters(TaskFilterDto taskFilterDto) {
        Specification<OurTask> spec = Specification.where(null);

        if (!taskFilterDto.email().isBlank()) {
            spec = spec.and(TaskSpecifications.hasEmail(taskFilterDto.email()));
        }
        if (taskFilterDto.status() != null) {
            spec = spec.and(TaskSpecifications.hasStatus(taskFilterDto.status()));
        }
        if (taskFilterDto.startDate() != null && taskFilterDto.endDate() != null) {
            spec = spec.and(TaskSpecifications.inDateRange(taskFilterDto.startDate(), taskFilterDto.endDate()));
        }
        if (StringUtils.hasText(taskFilterDto.title())) {
            spec = spec.and(TaskSpecifications.hasTitleLike(taskFilterDto.title()));
        }

        return ourTaskRepository.findAll(spec, PageRequest.of(taskFilterDto.page(), taskFilterDto.size()))
                .map(CustomOurTaskMapper::toResponseDto);
    }

    @Transactional
    @Override
    public OurTaskResponseDto getTaskById(Long id) {
        return CustomOurTaskMapper.toResponseDto(ourTaskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found. id=" + id)));
    }

    @Transactional
    @Override
    public OurTaskResponseDto assignPerformerToTask(Long taskId, Long userId) {
        OurTask ourTask = ourTaskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found. id=" + taskId));

        if (!userAccountRepository.existsById(userId)) {
            log.error("User account not found. id={}", userId);
            throw new UserEntityNotFound("User not found. id=" + userId);
        }

        UserAccount userAccount = userAccountRepository.getReferenceById(userId);
        ourTask.getPerformers().add(userAccount);
        userAccount.getOurTasks().add(ourTask);

        return CustomOurTaskMapper.toResponseDto(ourTaskRepository.save(ourTask));
    }

    @Transactional
    @Override
    public OurTaskResponseDto removePerformerFromTask(Long taskId, Long userId) {
        OurTask task = ourTaskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found. id=" + taskId));
        UserAccount userAccount = userAccountRepository.getReferenceById(userId);
        task.getPerformers().remove(userAccount);
        userAccount.getOurTasks().remove(task);

        return CustomOurTaskMapper.toResponseDto(ourTaskRepository.save(task));
    }

    @Transactional
    @Override
    public List<UserAccountShortDto> getPerformersFromTaskId(Long id) {
        OurTask task = ourTaskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found. id=" + id));

        if (task.getPerformers() != null && !task.getPerformers().isEmpty()) {
            return CustomUserAccountMapper.toShortDtos(task.getPerformers());
        }
        return List.of();
    }

    @Transactional
    @Override
    public OurTaskResponseDto setStatus(Long taskId, TaskStatus status) {
        OurTask task = ourTaskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found. id=" + taskId));
        task.setTaskStatus(status);

        return CustomOurTaskMapper.toResponseDto(ourTaskRepository.save(task));
    }

    @Transactional
    @Override
    public OurTaskResponseDto setPriority(Long taskId, TaskPriority priority) {
        OurTask task = ourTaskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found. id=" + taskId));
        task.setTaskPriority(priority);

        return CustomOurTaskMapper.toResponseDto(ourTaskRepository.save(task));
    }

    @Transactional
    @Override
    public OurTaskResponseDto updateTask(OurTaskRequestDto requestDto) {
        OurTask task = ourTaskRepository.findByTitle(requestDto.title())
                .orElseThrow(() -> new TaskNotFoundException("Task not found. Title: " + requestDto.title()));
        task.setDescription(requestDto.description());
        task.setTaskPriority(requestDto.priority());

        return CustomOurTaskMapper.toResponseDto(ourTaskRepository.save(task));
    }

    @Transactional
    @Override
    public OurTaskResponseDto addComment(String email, Long taskId, String comment) {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UserEntityNotFound("User not found. Email: " + email));
        OurTask ourTask = ourTaskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found. Id: "+ taskId));

        if (ourTask.getComments() == null) {
            ourTask.setComments(new ArrayList<>());
        }

        if (ourTask.getPerformers().contains(userAccount)
                || userAccount.getRole().name().equals(RoleUser.ADMIN.toString())) {
            TaskComment taskComment = TaskComment.builder().ourTask(ourTask).author(userAccount).text(comment).build();
            ourTask.getComments().add(taskComment);
        }
        return CustomOurTaskMapper.toResponseDto(ourTaskRepository.save(ourTask));
    }

    @Transactional
    @Override
    public OurTaskResponseDto removeComment(String email, Long taskId, Long commentId) {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UserEntityNotFound("User not found. Email: " + email));
        OurTask ourTask = ourTaskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found. Id: "+ taskId));

        if (ourTask.getPerformers().contains(userAccount)
                || userAccount.getRole().name().equals(RoleUser.ADMIN.toString())) {
            TaskComment taskComment = taskCommentRepository.findById(commentId)
                    .orElseThrow(() -> new EntityNotFoundException("Comment not found. id:" + commentId));
            ourTask.getComments().remove(taskComment);
        }
        return CustomOurTaskMapper.toResponseDto(ourTaskRepository.save(ourTask));
    }

    @Transactional
    @Override
    public Page<CommentResponseDto> getTaskComments(Long taskId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TaskComment> commentsPage = taskCommentRepository.findByOurTaskId(taskId, pageable);

        return commentsPage.map(CustomTaskCommentMapper::toDto);
    }

}
