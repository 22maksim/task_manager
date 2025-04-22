package system.task_management.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import system.task_management.model.properties.RedisProperties;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties properties;

    @Bean(name = "customRedisTemplate")
    public RedisTemplate<String, String> redisTemplate(
            @Qualifier("customRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean(name = "customRedisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(properties.host());
        redisStandaloneConfiguration.setPort(Integer.parseInt(properties.port()));
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

}
