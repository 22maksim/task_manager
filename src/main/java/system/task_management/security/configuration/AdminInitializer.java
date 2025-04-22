package system.task_management.security.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import system.task_management.exception.EmailAlreadyExistsException;
import system.task_management.security.model.RoleUser;
import system.task_management.security.model.dto.CreateUserRequestDto;
import system.task_management.security.service.UserAccountDetailsServiceImpl;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserAccountDetailsServiceImpl userAccountDetailsServiceImpl;

    @Override
    public void run(String... args) throws Exception {
        try {
            CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto();
            createUserRequestDto.setEmail("ouremail@mail.com");
            createUserRequestDto.setPassword("1408password");
            createUserRequestDto.setFirstName("Maks");
            createUserRequestDto.setLastName("Admin");
            userAccountDetailsServiceImpl.registerUser(createUserRequestDto, RoleUser.ADMIN);
        } catch (EmailAlreadyExistsException e) {
            System.out.println("Admin user already exists: " + e.getMessage());
        }
    }
}
