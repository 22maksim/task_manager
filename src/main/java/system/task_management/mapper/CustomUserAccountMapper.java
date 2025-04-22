package system.task_management.mapper;

import system.task_management.model.UserAccount;
import system.task_management.model.dto.OurTaskShortDto;
import system.task_management.model.dto.UserAccountResponseDto;
import system.task_management.model.dto.UserAccountShortDto;

import java.util.List;
import java.util.Objects;

public class CustomUserAccountMapper {

    public static UserAccountResponseDto toResponseDto(UserAccount userAccount) {
        if (userAccount == null) {
            return null;
        }
        return UserAccountResponseDto.builder()
                .id(userAccount.getId())
                .email(userAccount.getEmail())
                .firstName(userAccount.getFirstName())
                .lastName(userAccount.getLastName())
                .role(userAccount.getRole().name())
                .ourTasks(getListOurTasksShortDtos(userAccount))
                .createdAt(userAccount.getCreatedAt())
                .updatedAt(userAccount.getUpdatedAt())
                .build();
    }

    public static List<OurTaskShortDto> getListOurTasksShortDtos(UserAccount userAccount) {
        System.out.println("I am here two");
        if (userAccount == null || userAccount.getOurTasks() == null || userAccount.getOurTasks().isEmpty()) {
            System.out.println("I am here three");
            return List.of();
        }
        System.out.println("Запускаю преобразование задач в короткое описание <UNK> <UNK>");
        return userAccount.getOurTasks().stream()
                .filter(Objects::nonNull)
                .map(CustomOurTaskMapper::toShortDto)
                .toList();
    }

    public static UserAccountShortDto toShortDto(UserAccount userAccount) {
        return new UserAccountShortDto(
                userAccount.getId(), userAccount.getEmail(), userAccount.getFirstName(), userAccount.getLastName());
    }

    public static List<UserAccountShortDto> toShortDtos(List<UserAccount> userAccounts) {
        return userAccounts.stream()
                .map(CustomUserAccountMapper::toShortDto)
                .toList();
    }
}
