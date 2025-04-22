package system.task_management.model.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.data.redis")
public record RedisProperties(String port, String host, long expiration) {
}
