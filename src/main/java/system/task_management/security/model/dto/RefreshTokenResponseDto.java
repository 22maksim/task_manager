package system.task_management.security.model.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponseDto implements Serializable {

    String token;
    String refreshToken;

}
