package system.task_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import system.task_management.model.dto.UserAccountResponseDto;
import system.task_management.service.user.UserAccountService;

import java.util.List;

@Tag(name = "Users", description = "Операции с юзерами")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserAccountController {
    private final UserAccountService userAccountServiceImpl;

    @Operation(summary = "Получение юзера по email",
            description = "Позволяет получить пользователя по email. Открыт для всех авторизованых",
            tags = "Users"
    )
    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping("/email/{email}")
    public UserAccountResponseDto getUserAccountByEmail(@PathVariable @NotBlank String email) {
        return userAccountServiceImpl.getUserAccountByEmail(email);
    }

    @Operation(summary = "Получение юзера по id",
            description = "Позволяет получить пользователя по id. Открыт для всех авторизованых",
            tags = "Users"
    )
    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping("/id/{id}")
    public UserAccountResponseDto getUserAccountById(@PathVariable @Positive long id) {
        return userAccountServiceImpl.getUserAccountById(id);
    }

    @Operation(summary = "Получение списка юзеров по списку ids",
            description = "Позволяет получить список юзеров по списку их id. Открыт для всех авторизованых",
            tags = "Users"
    )
    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping
    public List<UserAccountResponseDto> getAllUserAccountsByIds(@RequestParam(name = "ids") @NotEmpty List<Long> ids) {
        return userAccountServiceImpl.getAllUserAccountsByIds(ids);
    }
}
