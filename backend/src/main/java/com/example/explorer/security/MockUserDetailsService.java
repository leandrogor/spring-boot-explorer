package com.example.explorer.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MockUserDetailsService implements UserDetailsService {

    private final Map<String, UserDetails> users = new HashMap<>();

    public MockUserDetailsService() {
        users.put("admin", User.builder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN")
                .build());
        users.put("user1", User.builder()
                .username("user1")
                .password("user123")
                .roles("USER")
                .build());
        users.put("user2", User.builder()
                .username("user2")
                .password("user123")
                .roles("USER")
                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        return user;
    }
}
