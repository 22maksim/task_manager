package system.task_management.service.user;

import system.task_management.model.dto.UserAccountResponseDto;

import java.util.List;

public interface UserAccountService {

    UserAccountResponseDto getUserAccountByEmail(String email);

    UserAccountResponseDto getUserAccountById(long id);

    List<UserAccountResponseDto> getAllUserAccountsByIds(List<Long> ids);
}