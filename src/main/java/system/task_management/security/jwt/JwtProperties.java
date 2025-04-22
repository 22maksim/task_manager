package system.task_management.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("properties.jwt")
public class JwtProperties {
    private String secret;
    private long expirationTime;
    private long refreshInterval;
}
