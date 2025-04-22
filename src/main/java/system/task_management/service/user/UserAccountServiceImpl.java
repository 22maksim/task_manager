package system.task_management.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import system.task_management.exception.UserEntityNotFound;
import system.task_management.mapper.CustomUserAccountMapper;
import system.task_management.model.dto.UserAccountResponseDto;
import system.task_management.repository.UserAccountRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepository userAccountRepository;

    @Override
    public UserAccountResponseDto getUserAccountByEmail(String email) {
        return CustomUserAccountMapper.toResponseDto(userAccountRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email {}", email);
                    return new UserEntityNotFound("User not found");
                }));
    }

    @Override
    public UserAccountResponseDto getUserAccountById(long id) {
        return CustomUserAccountMapper.toResponseDto(userAccountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with. Id {}", id);
                    return new UserEntityNotFound("User not found");
                }));
    }

    @Override
    public List<UserAccountResponseDto> getAllUserAccountsByIds(List<Long> ids) {
        return userAccountRepository.findAllById(ids).stream()
                .filter(Objects::nonNull)
                .map(CustomUserAccountMapper::toResponseDto)
                .toList();
    }
}
