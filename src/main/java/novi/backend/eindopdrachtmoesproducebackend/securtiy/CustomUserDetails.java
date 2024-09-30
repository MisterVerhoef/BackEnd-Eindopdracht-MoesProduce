package novi.backend.eindopdrachtmoesproducebackend.securtiy;

import novi.backend.eindopdrachtmoesproducebackend.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;


public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<User.Role> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            System.out.println("Warning: User roles are null or empty for user: " + user.getUsername());
            return Collections.emptyList();
        }
        roles.forEach(role -> {
            System.out.println("Role name: " + role.name());
            System.out.println("Logged in: " + user.getUsername() + " with role: " + role.name());
        });
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();

    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
