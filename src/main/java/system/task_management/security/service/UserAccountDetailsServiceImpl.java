package system.task_management.security.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import system.task_management.exception.*;
import system.task_management.mapper.CustomUserAccountMapper;
import system.task_management.model.UserAccount;
import system.task_management.model.enums.UserAccountStatus;
import system.task_management.repository.UserAccountRepository;
import system.task_management.security.jwt.JwtProperties;
import system.task_management.security.jwt.JwtUtil;
import system.task_management.security.model.RefreshToken;
import system.task_management.security.model.RoleUser;
import system.task_management.security.model.UserAccountDetails;
import system.task_management.security.model.dto.*;
import system.task_management.security.repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountDetailsServiceImpl implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAccountRepository.findByEmail(username)
                .map(UserAccountDetails::fromUserAccount)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Transactional
    public RegisterResponseDto registerUser(CreateUserRequestDto requestRegisterDto, RoleUser roleUser) {
        if (userAccountRepository.existsByEmail(requestRegisterDto.getEmail())) {
            log.error("Email already exists.");
            throw new EmailAlreadyExistsException(requestRegisterDto.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(requestRegisterDto.getPassword());
        UserAccount userAccount = UserAccount.builder()
                .email(requestRegisterDto.getEmail())
                .password(encodedPassword)
                .firstName(requestRegisterDto.getFirstName())
                .lastName(requestRegisterDto.getLastName())
                .role(roleUser)
                .userAccountStatus(UserAccountStatus.ACTIVE)
                .build();
        userAccount = userAccountRepository.save(userAccount);
        String token = jwtUtil.generateToken(userAccount);

        log.info("User account register is successful. Email: {}.", userAccount.getEmail());

        return new RegisterResponseDto(token, userAccount.getEmail());
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto requestLoginDto) {
        UserAccount userAccount = userAccountRepository.findByEmail(requestLoginDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Данный пользователь не зарегистрирован. Email:" + requestLoginDto.getEmail()));
        if (!passwordEncoder.matches(requestLoginDto.getPassword(), userAccount.getPassword())) {
            throw new IllegalArgumentException("Неверный пароль");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByEmail(requestLoginDto.getEmail())
                .orElse(null);
        if (refreshToken != null) {
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiresAt(Instant.now().plusMillis(jwtProperties.getRefreshInterval()));
        } else {
            refreshToken = RefreshToken.builder()
                    .token(UUID.randomUUID().toString())
                    .email(requestLoginDto.getEmail())
                    .expiresAt(Instant.now().plusMillis(jwtProperties.getRefreshInterval()))
                    .build();
        }
        refreshToken = refreshTokenRepository.save(refreshToken);

        log.info("User login. Email: {}.", userAccount.getEmail());

        return LoginResponseDto.builder()
                .userAccountResponseDto(CustomUserAccountMapper.toResponseDto(userAccount))
                .token(jwtUtil.generateToken(userAccount))
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public RefreshTokenResponseDto refreshAccessToken(RefreshTokenRequestDto requestDto) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestDto.getRefreshToken())
                .orElseThrow(() -> new RefreshTokenEntityNotFound(
                        "Token not found"));

        if (!refreshToken.getEmail().equals(requestDto.getEmail())) {
            log.error("Email from UserAccount and RefreshRequest Email do not match");
            throw new EmailAlreadyExistsException(requestDto.getEmail());
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.ofEpochSecond(jwtProperties.getRefreshInterval()))) {
            log.info("Refresh token expired.");
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenExpiredException("Refresh token expired");
        }

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(Instant.now().plusMillis(jwtProperties.getRefreshInterval()));

        refreshToken = refreshTokenRepository.save(refreshToken);

        String newAccessToken = jwtUtil.generateToken(userAccountRepository.findByEmail(requestDto.getEmail())
        .orElseThrow(() -> new UserEntityNotFound("User not found")));
        log.info("Successfully issued aces_token for user: {}", refreshToken.getEmail());

        return RefreshTokenResponseDto.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public void logout(RefreshTokenRequestDto refreshDto) {
        UserAccount userAccount = userAccountRepository.findByEmail(refreshDto.getEmail())
                .orElseThrow(() -> new UserEntityNotFound("User not found. Email: " + refreshDto.getEmail()));

        if (userAccount.getEmail().equals(refreshDto.getEmail())) {
            refreshTokenRepository.deleteByToken(refreshDto.getRefreshToken());
        } else {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
    }
}
