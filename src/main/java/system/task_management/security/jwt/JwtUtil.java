package system.task_management.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import system.task_management.model.UserAccount;
import system.task_management.security.model.Permission;
import system.task_management.security.model.RoleUser;
import system.task_management.service.redis.RedisService;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;

    public String generateToken(UserAccount userAccount) {
        String email = userAccount.getEmail();
        Map<String, Object> claims = getAccessesByRole(userAccount.getRole());
        Instant now = Instant.now();

        if (!StringUtils.hasText(email)) {
            log.warn("Generate JWT token failed, email: {}, claims: {}", email, claims);
            throw new JwtException("Generate JWT token failed, email: " + email);
        }

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(jwtProperties.getExpirationTime())))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);

            if (isTokenExpired(token)) {
                log.warn("Token is expired: {}", token);
                return false;
            }

            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            if (!StringUtils.hasText(email) || !StringUtils.hasText(role)) {
                log.warn("Token is missing required claims: email={}, role={}", email, role);
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        log.warn("Authorization header is missing or invalid: {}", authHeader);
        return "";
    }

    private Map<String, Object> getAccessesByRole(RoleUser roleUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_" + roleUser.name());

        List<String> permissions = roleUser.getPermissions().stream()
                .map(Permission::getPermission)
                .toList();
        claims.put("permissions", permissions);

        return claims;
    }

    public Optional<String> extractEmailIfValid(String token) {
        if (!validateToken(token)) return Optional.empty();
        try {
            return Optional.ofNullable(extractEmail(token));
        } catch (Exception e) {
            log.warn("Failed to extract email from token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public String extractEmail(String token) {
        if (token == null || token.isEmpty()) {
            log.warn("Email don't extract. Token is null or empty");
            throw new IllegalArgumentException("Email don't extract. Token is null or empty");
        }
        return Objects.requireNonNull(extractClaims(token)).getSubject();
    }

    public Claims extractClaims(String token) {
        if (token == null || token.isEmpty()) {
            log.warn("Token is null or empty");
            throw new IllegalArgumentException("Token is null or empty");
        }
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        if (token == null || token.isEmpty()) {
            log.warn("Token for lifetime check is null");
            throw new IllegalArgumentException("Token for lifetime check is null");
        }
        return Objects.requireNonNull(extractClaims(token)).getExpiration().before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] encodeKey = Base64.getDecoder().decode(jwtProperties.getSecret());
        if (encodeKey == null) {
            log.error("Secret key is null");
            throw new SecurityException("Secret key is null");
        }
        return Keys.hmacShaKeyFor(encodeKey);
    }
}
