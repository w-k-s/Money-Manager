package io.wks.moneymanager.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Making this class into a record makes its properties inaccessible to Casbin
 */
public class DefaultUser implements UserDetails {

    private final String username;
    private final String password;
    private final String role;
    private final List<SimpleGrantedAuthority> authorities;
    private final boolean enabled;

    DefaultUser(String username,
                String password,
                String role,
                List<SimpleGrantedAuthority> authorities,
                boolean enabled) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        Objects.requireNonNull(role);
        this.username = username;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Optional.ofNullable(authorities)
                .orElse(Collections.emptyList());
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
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getRole() {
        return role;
    }
}
