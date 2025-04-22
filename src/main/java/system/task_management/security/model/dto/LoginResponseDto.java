package system.task_management.security.model.dto;

import lombok.Builder;
import lombok.Getter;
import system.task_management.model.dto.UserAccountResponseDto;

@Getter
@Builder
public class LoginResponseDto {
    private UserAccountResponseDto userAccountResponseDto;
    private String token;
    private String refreshToken;
}
