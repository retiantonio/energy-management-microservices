package com.retiantonio.device_service.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@RequiredArgsConstructor
public class GatewayAuthFilter extends OncePerRequestFilter {

    private final String internalKey;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String key = req.getHeader("X-Internal-Key");
        String userId = req.getHeader("X-User-Id");
        String role = req.getHeader("X-User-Role");

        if (key != null && MessageDigest.isEqual(
                key.getBytes(StandardCharsets.UTF_8), internalKey.getBytes(StandardCharsets.UTF_8))) {
            // service-to-service call
            authenticate("internal", "INTERNAL");
        } else if (userId != null && ("ADMIN".equals(role) || "CLIENT".equals(role))) {
            // request that went through Traefik ForwardAuth
            authenticate(userId, role);
        }
        chain.doFilter(req, res);
    }

    private void authenticate(String principal, String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }
}
