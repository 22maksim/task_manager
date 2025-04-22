package system.task_management.security.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDto implements Serializable {

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Синтаксис email не правильный")
    private String email;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 8, max = 255, message = "Пароль должен быть не менее 8 символов")
    private String password;

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 3, max = 255, message = "Имя должно быть не короче 3 символов")
    private String firstName;

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 3, max = 255, message = "Имя должно быть не короче 3 символов")
    private String lastName;

}
