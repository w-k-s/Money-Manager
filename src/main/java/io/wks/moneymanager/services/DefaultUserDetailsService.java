package io.wks.moneymanager.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

public class DefaultUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (!s.equals("admin")) throw new UsernameNotFoundException(s);
        return new DefaultUser("admin", "password", Collections.emptyList(), true);
    }
}
