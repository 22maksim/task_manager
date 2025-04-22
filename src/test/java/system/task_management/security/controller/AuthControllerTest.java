package system.task_management.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import system.task_management.model.dto.UserAccountResponseDto;
import system.task_management.repository.UserAccountRepository;
import system.task_management.security.jwt.JwtAuthFilter;
import system.task_management.security.jwt.JwtUtil;
import system.task_management.security.model.RoleUser;
import system.task_management.security.model.dto.*;
import system.task_management.security.service.UserAccountDetailsServiceImpl;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAccountDetailsServiceImpl userAccountDetailsService;

    @MockitoBean
    private UserAccountRepository userAccountRepository;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUserShouldReturnToken() throws Exception {
        // given
        CreateUserRequestDto request = new CreateUserRequestDto();
        request.setEmail("testuser@test.com");
        request.setPassword("password");
        request.setFirstName("one-test");
        request.setLastName("two-test");

        RegisterResponseDto response = new RegisterResponseDto("jwt-token", "testuser@test.com");

        Mockito.when(userAccountDetailsService.registerUser(Mockito.any(), Mockito.eq(RoleUser.USER)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/security/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_ReturnsTokenAndUserDetails() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("user@example.com", "password123");

        UserAccountResponseDto userAccountDto = UserAccountResponseDto.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(RoleUser.USER.name())
                .ourTasks(Collections.emptyList())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        LoginResponseDto responseDto = LoginResponseDto.builder()
                .userAccountResponseDto(userAccountDto)
                .token("access-token")
                .refreshToken("refresh-token")
                .build();

        Mockito.when(userAccountDetailsService.login(any(LoginRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/security/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer access-token"))
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.userAccountResponseDto.email").value("user@example.com"))
                .andExpect(jsonPath("$.userAccountResponseDto.firstName").value("John"))
                .andExpect(jsonPath("$.userAccountResponseDto.lastName").value("Doe"))
                .andExpect(jsonPath("$.userAccountResponseDto.role").value("USER"));
    }

    @Test
    void refresh_ReturnsNewAccessToken() throws Exception {
        RefreshTokenRequestDto refreshDto = new RefreshTokenRequestDto("refresh-token", "user@example.com");
        RefreshTokenResponseDto responseDto = new RefreshTokenResponseDto("new-access-token", "refresh-token");

        Mockito.when(userAccountDetailsService.refreshAccessToken(any(RefreshTokenRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/security/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshDto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer new-access-token"))
                .andExpect(jsonPath("$.token").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void logout_ReturnsNoContent() throws Exception {
        RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto("refresh-token", "user@example.com");

        mockMvc.perform(delete("/api/v1/security/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent());
    }
}
