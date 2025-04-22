package system.task_management.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import system.task_management.repository.UserAccountRepository;
import system.task_management.security.model.UserAccountDetails;
import system.task_management.service.redis.RedisService;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final UserAccountRepository userAccountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = jwtUtil.extractTokenFromRequest(request);

        if (jwtUtil.validateToken(jwt) && !redisService.isTokenBlacklisted(jwt)) {
            Optional<String> emailOpt = jwtUtil.extractEmailIfValid(jwt);

            if (emailOpt.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = emailOpt.get();
                try {
                    UserDetails userDetails = UserAccountDetails.fromUserAccount(userAccountRepository.findByEmail(email)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found. Email: " + email)));

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                } catch (UsernameNotFoundException ex) {
                    log.warn("User not found for email: {}", email);
                } catch (Exception ex) {
                    log.error("Auth filter error on URI: {}", request.getRequestURI(), ex);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
