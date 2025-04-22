package system.task_management.security.model;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum RoleUser {

    ADMIN(Set.of(
            Permission.READ, Permission.WRITE, Permission.UPDATE,
            Permission.DELETE, Permission.COMMENT, Permission.STATUS
    )),
    USER(Set.of(Permission.COMMENT, Permission.STATUS));

    private final Set<Permission> permissions;

    RoleUser(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}