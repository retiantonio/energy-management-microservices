package com.retiantonio.authentication_service.service;

import com.retiantonio.authentication_service.model.Credential;
import com.retiantonio.authentication_service.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CredentialUserDetailsService implements UserDetailsService {

    private final CredentialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Credential c = credentialRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                c.getUsername(),
                c.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + c.getRole().name()))
        );
    }
}