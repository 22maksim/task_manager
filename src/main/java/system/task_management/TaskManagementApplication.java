package system.task_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import system.task_management.model.properties.RedisProperties;
import system.task_management.security.jwt.JwtProperties;

@EnableConfigurationProperties({RedisProperties.class, JwtProperties.class})
@SpringBootApplication(scanBasePackages = "system.task_management")
public class TaskManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagementApplication.class, args);
	}

}
