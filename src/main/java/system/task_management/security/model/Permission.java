package system.task_management.security.model;

import lombok.Getter;

@Getter
public enum Permission {
    WRITE("permission:write"),
    READ("permission:read"),
    UPDATE("permission:update"),
    COMMENT("permission:comment"),
    STATUS("permission:status"),
    DELETE("permission:delete");
    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }
}