package system.task_management.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import system.task_management.security.model.RoleUser;
import system.task_management.security.model.dto.*;
import system.task_management.security.service.UserAccountDetailsServiceImpl;

@Tag(name = "Аутентификация", description = "Регистрация и логин пользователей")
@RestController
@RequestMapping("api/v1/security")
@RequiredArgsConstructor
public class AuthController {
    private final UserAccountDetailsServiceImpl userAccountDetailsService;

    @Operation(
            summary = "Регистрация обычного пользователя",
            description = "Создаёт нового пользователя в системе и возвращает JWT токен"
    )
    @PostMapping("/register/user")
    public RegisterResponseDto registerUser(
            @RequestBody @Valid @NotNull(message = "Пришел пустой запрос") CreateUserRequestDto requestRegisterDto) {
        return userAccountDetailsService.registerUser(requestRegisterDto, RoleUser.USER);
    }

    @Operation(
            summary = "Регистрация администратора",
            description = "Создаёт нового администратора в системе (доступно только для ADMIN'ов)"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/admin")
    public RegisterResponseDto registerAdmin(
            @RequestBody @Valid @NotNull(message = "Пришел пустой запрос") CreateUserRequestDto requestRegisterDto) {
        return userAccountDetailsService.registerUser(requestRegisterDto, RoleUser.ADMIN);
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Авторизует пользователя. Возвращает JWT и refresh токены вместе с данными пользователя"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid @NotNull(message = "Пришел пустой запрос") LoginRequestDto loginRequestDto) {
        var response = userAccountDetailsService.login(loginRequestDto);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + response.getToken())
                .body(response);
    }

    @Operation(
            summary = "Выход из системы",
            description = "Удаляет refresh токен из системы, access токен становится невалидным при следующем запросе"
    )
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody @Valid @NotNull(message = "Пришел пустой запрос") RefreshTokenRequestDto refreshDto) {
        userAccountDetailsService.logout(refreshDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Обновление access токена",
            description = "Выдает новый access токен при валидном refresh токене"
    )
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(
            @RequestBody @Valid @NotNull(message = "Пришел пустой запрос") RefreshTokenRequestDto refreshDto) {
        var responseDto = userAccountDetailsService.refreshAccessToken(refreshDto);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + responseDto.getToken())
                .body(responseDto);
    }
}
