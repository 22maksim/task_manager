package system.task_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import system.task_management.model.dto.OurTaskRequestDto;
import system.task_management.model.dto.OurTaskResponseDto;
import system.task_management.model.enums.TaskPriority;
import system.task_management.model.enums.TaskStatus;
import system.task_management.security.jwt.JwtAuthFilter;
import system.task_management.security.jwt.JwtUtil;
import system.task_management.service.task.OurTaskService;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private OurTaskService ourTaskServiceImpl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createTaskTest() throws Exception {
        OurTaskRequestDto requestDto = OurTaskRequestDto.builder()
                .authorEmail("author@email.com")
                .title("title")
                .description("description")
                .priority(TaskPriority.MEDIUM)
                .build();
        OurTaskResponseDto responseDto = OurTaskResponseDto.builder()
                .id(1L)
                .title("title")
                .description("description")
                .performers(List.of())
                .taskPriority(TaskPriority.MEDIUM)
                .taskStatus(TaskStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Mockito.when(ourTaskServiceImpl.createTask(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(requestDto.title()));
    }
}
