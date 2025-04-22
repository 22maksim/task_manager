package system.task_management.service.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import system.task_management.mapper.CustomOurTaskMapper;
import system.task_management.mapper.CustomTaskCommentMapper;
import system.task_management.model.OurTask;
import system.task_management.model.TaskComment;
import system.task_management.model.UserAccount;
import system.task_management.model.dto.CommentResponseDto;
import system.task_management.model.dto.OurTaskRequestDto;
import system.task_management.model.dto.OurTaskResponseDto;
import system.task_management.model.dto.TaskFilterDto;
import system.task_management.model.enums.TaskPriority;
import system.task_management.model.enums.TaskStatus;
import system.task_management.repository.OurTaskRepository;
import system.task_management.repository.TaskCommentRepository;
import system.task_management.repository.UserAccountRepository;
import system.task_management.security.model.RoleUser;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class OurTaskServiceTest {

    @Mock
    private TaskCommentRepository taskCommentRepository;

    @Mock
    private OurTaskRepository ourTaskRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private OurTaskServiceImpl ourTaskService;



    @Test
    void createTask_shouldCreateAndReturnTask() {
        // given
        String email = "test@email.com";
        OurTaskRequestDto requestDto = new OurTaskRequestDto("test@email.com", "Title", email, TaskPriority.HIGH);

        UserAccount user = UserAccount.builder()
                .id(1L)
                .email(email)
                .role(RoleUser.USER)
                .build();

        OurTask ourTask = new OurTask();
        ourTask.setTitle("Title");
        ourTask.setAuthor(user);
        ourTask.setDescription("Description");
        ourTask.setTaskStatus(TaskStatus.PENDING);
        ourTask.setTaskPriority(TaskPriority.HIGH);

        Mockito.when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(ourTaskRepository.save(ourTask)).thenReturn(ourTask);

        OurTaskResponseDto expectedResponse = OurTaskResponseDto.builder()
                .emailAuthor(ourTask.getAuthor().getEmail())
                .title(ourTask.getTitle())
                .taskStatus(ourTask.getTaskStatus())
                .build();

        try (MockedStatic<CustomOurTaskMapper> mapperMock = Mockito.mockStatic(CustomOurTaskMapper.class)) {
            mapperMock.when(() -> CustomOurTaskMapper.createOurTask(requestDto)).thenReturn(ourTask);
            mapperMock.when(() -> CustomOurTaskMapper.toResponseDto(ourTask)).thenReturn(expectedResponse);

            Mockito.when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(user));

            OurTaskResponseDto result = ourTaskService.createTask(requestDto);

            assertEquals("Title", result.title());
            assertEquals(TaskStatus.PENDING, result.taskStatus());
            assertEquals(email, result.emailAuthor());
        }
    }

    @Test
    void getTasksByFilters_shouldReturnFilteredTasks() {
        TaskFilterDto filterDto = new TaskFilterDto(
                "user@email.com",
                TaskStatus.PENDING,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "Title",
                0,
                5
        );

        OurTask task = new OurTask();
        task.setId(1L);
        task.setTitle("Title");
        task.setDescription("Description");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setTaskPriority(TaskPriority.HIGH);

        UserAccount author = new UserAccount();
        author.setEmail("user@email.com");
        task.setAuthor(author);

        Page<OurTask> page = new PageImpl<>(List.of(task));

        OurTaskResponseDto expectedDto = OurTaskResponseDto.builder()
                .emailAuthor(author.getEmail())
                .title(task.getTitle())
                .taskStatus(TaskStatus.PENDING)
                .build();

        try (MockedStatic<CustomOurTaskMapper> mapperMock = Mockito.mockStatic(CustomOurTaskMapper.class)) {
            mapperMock.when(() -> CustomOurTaskMapper.toResponseDto(task)).thenReturn(expectedDto);

            Mockito.when(ourTaskRepository.findAll((Mockito.<Specification<OurTask>>any()), any(PageRequest.class)))
                    .thenReturn(page);

            Page<OurTaskResponseDto> result = ourTaskService.getTasksByFilters(filterDto);

            assertEquals(1, result.getTotalElements());
            OurTaskResponseDto dto = result.getContent().get(0);
            assertEquals("Title", dto.title());
            assertEquals(TaskStatus.PENDING, dto.taskStatus());
            assertEquals("user@email.com", dto.emailAuthor());
        }
    }

    @Test
    void assignPerformerToTask_shouldAssignUserAndReturnUpdatedTask() {
        // given
        Long taskId = 1L;
        Long userId = 2L;

        UserAccount performer = UserAccount.builder()
                .id(userId)
                .email("performer@email.com")
                .role(RoleUser.USER)
                .ourTasks(new ArrayList<>())
                .build();

        OurTask ourTask = new OurTask();
        ourTask.setId(taskId);
        ourTask.setTitle("Task Title");
        ourTask.setTaskStatus(TaskStatus.PENDING);
        ourTask.setPerformers(new ArrayList<>());

        OurTask updatedTask = new OurTask();
        updatedTask.setId(taskId);
        updatedTask.setTitle("Task Title");
        updatedTask.setTaskStatus(TaskStatus.PENDING);
        updatedTask.setPerformers(List.of(performer));

        OurTaskResponseDto expectedDto = OurTaskResponseDto.builder()
                .id(taskId)
                .title(updatedTask.getTitle())
                .build();

        Mockito.when(ourTaskRepository.findById(taskId)).thenReturn(Optional.of(ourTask));
        Mockito.when(userAccountRepository.existsById(userId)).thenReturn(true);
        Mockito.when(userAccountRepository.getReferenceById(userId)).thenReturn(performer);
        Mockito.when(ourTaskRepository.save(any(OurTask.class))).thenReturn(updatedTask);

        try (MockedStatic<CustomOurTaskMapper> mapperMock = Mockito.mockStatic(CustomOurTaskMapper.class)) {
            mapperMock.when(() -> CustomOurTaskMapper.toResponseDto(updatedTask)).thenReturn(expectedDto);

            OurTaskResponseDto result = ourTaskService.assignPerformerToTask(taskId, userId);

            assertEquals(taskId, result.id());
            assertEquals("Task Title", result.title());
            Mockito.verify(ourTaskRepository).save(any(OurTask.class));
            Mockito.verify(userAccountRepository).getReferenceById(userId);
        }
    }

    @Test
    void addComment_shouldAddCommentIfUserIsPerformer() {
        String email = "user@email.com";
        Long taskId = 1L;
        String commentText = "This is a comment";

        UserAccount user = UserAccount.builder()
                .id(2L)
                .email(email)
                .role(RoleUser.USER)
                .build();

        OurTask task = new OurTask();
        task.setId(taskId);
        task.setPerformers(List.of(user));
        task.setComments(new ArrayList<>());

        TaskComment newComment = TaskComment.builder()
                .author(user)
                .text(commentText)
                .ourTask(task)
                .build();

        OurTask updatedTask = new OurTask();
        updatedTask.setId(taskId);
        updatedTask.setPerformers(task.getPerformers());
        updatedTask.setComments(List.of(newComment));

        OurTaskResponseDto expectedDto = OurTaskResponseDto.builder()
                .emailAuthor(email)
                .build();

        Mockito.when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(ourTaskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(ourTaskRepository.save(any())).thenReturn(updatedTask);

        try (MockedStatic<CustomOurTaskMapper> mapperMock = Mockito.mockStatic(CustomOurTaskMapper.class)) {
            mapperMock.when(() -> CustomOurTaskMapper.toResponseDto(updatedTask)).thenReturn(expectedDto);

            OurTaskResponseDto result = ourTaskService.addComment(email, taskId, commentText);

            assertEquals(email, result.emailAuthor());
            Mockito.verify(ourTaskRepository).save(any());
        }
    }

    @Test
    void removeComment_shouldRemoveCommentIfUserIsPerformer() {
        String email = "user@email.com";
        Long taskId = 1L;
        Long commentId = 10L;

        UserAccount user = UserAccount.builder()
                .id(2L)
                .email(email)
                .role(RoleUser.USER)
                .build();

        TaskComment comment = TaskComment.builder()
                .id(commentId)
                .author(user)
                .text("Test comment")
                .build();

        OurTask task = new OurTask();
        task.setId(taskId);
        task.setPerformers(List.of(user));
        task.setComments(new ArrayList<>());
        task.getComments().add(comment);

        OurTask updatedTask = new OurTask();
        updatedTask.setId(taskId);
        updatedTask.setPerformers(task.getPerformers());
        updatedTask.setComments(new ArrayList<>());

        OurTaskResponseDto expectedDto = OurTaskResponseDto.builder()
                .emailAuthor(email)
                .build();

        Mockito.when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(ourTaskRepository.findById(taskId)).thenReturn(Optional.of(task));
        Mockito.when(taskCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        Mockito.when(ourTaskRepository.save(any())).thenReturn(updatedTask);

        try (MockedStatic<CustomOurTaskMapper> mapperMock = Mockito.mockStatic(CustomOurTaskMapper.class)) {
            mapperMock.when(() -> CustomOurTaskMapper.toResponseDto(updatedTask)).thenReturn(expectedDto);

            OurTaskResponseDto result = ourTaskService.removeComment(email, taskId, commentId);

            assertEquals(email, result.emailAuthor());
            Mockito.verify(taskCommentRepository).findById(commentId);
            Mockito.verify(ourTaskRepository).save(any());
        }
    }

    @Test
    void getTaskComments_shouldReturnPagedComments() {
        Long taskId = 1L;
        int page = 0;
        int size = 2;

        TaskComment comment1 = TaskComment.builder().id(1L).text("Comment 1").build();
        TaskComment comment2 = TaskComment.builder().id(2L).text("Comment 2").build();

        Page<TaskComment> commentPage = new PageImpl<>(List.of(comment1, comment2));

        CommentResponseDto dto1 = new CommentResponseDto(1L, "Comment 1", null, null);
        CommentResponseDto dto2 = new CommentResponseDto(2L, "Comment 2", null, null);

        Mockito.when(taskCommentRepository.findByOurTaskId(eq(taskId), any(Pageable.class))).thenReturn(commentPage);

        try (MockedStatic<CustomTaskCommentMapper> commentMapper = Mockito.mockStatic(CustomTaskCommentMapper.class)) {
            commentMapper.when(() -> CustomTaskCommentMapper.toDto(comment1)).thenReturn(dto1);
            commentMapper.when(() -> CustomTaskCommentMapper.toDto(comment2)).thenReturn(dto2);

            Page<CommentResponseDto> result = ourTaskService.getTaskComments(taskId, page, size);

            assertEquals(2, result.getContent().size());
            assertEquals("Comment 1", result.getContent().get(0).comment());
            assertEquals("Comment 2", result.getContent().get(1).comment());
        }
    }

}
