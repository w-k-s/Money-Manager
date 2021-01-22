package io.wks.moneymanager.services;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultUserDetailsService implements UserDetailsService {

    private static Map<String, DefaultUser> usersStore = new ConcurrentHashMap<>();

    public DefaultUserDetailsService() {
        usersStore.put("admin", new DefaultUser("admin", "password", "admin", Collections.singletonList(new SimpleGrantedAuthority("ADMIN")), true));
        usersStore.put("user", new DefaultUser("user", "password", "user", Collections.singletonList(new SimpleGrantedAuthority("USER")), true));
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return Optional.ofNullable(usersStore.get(s))
                .orElseThrow(() -> new UsernameNotFoundException(s));
    }
}
