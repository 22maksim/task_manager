package system.task_management.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import system.task_management.model.UserAccount;
import system.task_management.model.enums.UserAccountStatus;

import java.util.Collection;
import java.util.List;

public class UserAccountDetails implements UserDetails {

    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;
    private final boolean isActive;

    public UserAccountDetails(
            String username, String password, List<SimpleGrantedAuthority> authorities, boolean isActive) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.isActive = isActive;
    }

    public static UserAccountDetails fromUserAccount(UserAccount userAccount) {
        return new UserAccountDetails(
                userAccount.getEmail(), // просили использовать email
                userAccount.getPassword(),
                List.copyOf(userAccount.getRole().getAuthorities()),
                userAccount.getUserAccountStatus().equals(UserAccountStatus.ACTIVE)
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
