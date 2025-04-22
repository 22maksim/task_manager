package system.task_management.service.redis;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import system.task_management.model.properties.RedisProperties;
import system.task_management.security.jwt.JwtUtil;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {
    private final JwtUtil jwtUtil;
    private final RedisProperties properties;
    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(
            JwtUtil jwtUtil, RedisProperties properties,
            @Qualifier("customRedisTemplate") RedisTemplate<String, String> redisTemplate
    ) {
        this.jwtUtil = jwtUtil;
        this.properties = properties;
        this.redisTemplate = redisTemplate;
    }

    /**
     *
     * If the user is blocked or logs out, then we add the token to the blacklist
     * @param request
     */
    public void addTokenToBlackList(HttpServletRequest request) {
        if (request.getHeader("Authorization") != null) {
            String token = request.getHeader("Authorization").substring(7);
            String email = jwtUtil.extractEmail(token);

            putTokenToBlacklist(token);

            log.info("Added token to blacklist. Email: {}", email);
        } else {
            log.warn(
                    "No Authorization header found in request. Bad added token to blacklist. Request: {}",
                    request.getHeaderNames()
            );
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }


    private void putTokenToBlacklist(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }

        String key = "blacklist:" + token;

        if (Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(key, "blacklisted", properties.expiration(), TimeUnit.SECONDS))) {
            log.info("Token added to blacklist: {}", token);
        } else {
            log.warn("Failed to add token to blacklist: {}", token);
        }
    }
}
